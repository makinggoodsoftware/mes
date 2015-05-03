package com.mgs.mes.v3;

import com.mgs.reflection.*;

import java.util.*;

import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.stream.Collectors.toList;

public class MapEntityContext {
	private final ManagerLocator managerLocator;
	private final FieldAccessorParser fieldAccessorParser;
	private final BeanNamingExpert beanNamingExpert;
	private final Reflections reflections;

	public MapEntityContext(ManagerLocator managerLocator, FieldAccessorParser fieldAccessorParser, BeanNamingExpert beanNamingExpert, Reflections reflections) {
		this.managerLocator = managerLocator;
		this.fieldAccessorParser = fieldAccessorParser;
		this.beanNamingExpert = beanNamingExpert;
		this.reflections = reflections;
	}

	public <T extends MapEntity> T transform(Map<String, Object> data, Class<T> type) {
		Map<String, Object> domainMap = new HashMap<>();
		fieldAccessorParser.parse(type).
				filter(this::isAGetter).
				forEach((accessor) -> {
					String fieldName = beanNamingExpert.getFieldName(accessor.getMethodName(), "get");
					Object value = domainValue(accessor, data.get(fieldName));
					domainMap.put(accessor.getMethodName(), value);
				});

		//noinspection unchecked
		return (T) newProxyInstance(
				MapEntityContext.class.getClassLoader(),
				new Class[]{type},
				new EntityMapCallInterceptor<>(
						type,
						domainMap,
						managerLocator.byType(type)
				)
		);
	}

	private Object domainValue(FieldAccessor accessor, Object rawValue) {
		assertNoOptionalFieldIsSet(accessor, rawValue);

		Class<?> declaredType = accessor.getDeclaredType();
		List<ParametrizedType> parametrizedTypes = accessor.getParametrizedTypes();
		if (reflections.isSimple(declaredType)) return rawValue;
		if (reflections.isAssignableTo(declaredType, MapEntity.class)) {
			//noinspection unchecked
			Map<String, Object> castedValue = (Map<String, Object>) rawValue;
			//noinspection unchecked
			Class<? extends MapEntity> castedType = (Class<? extends MapEntity>) declaredType;
			return transform(castedValue, castedType);
		}
		if (reflections.isCollection(declaredType)) {
			List castedValue = (List) rawValue;
			Class typeOfCollection = parametrizedTypes.get(0).getSpecificClass().get();
			//noinspection unchecked
			return castedValue.stream().map((old) -> mapValue(typeOfCollection, new ArrayList<>(), old)).collect(toList());
		}
		if (reflections.isAssignableTo(declaredType, Optional.class)) {
			if (rawValue == null) return Optional.empty();
			Class typeOfOptional = parametrizedTypes.get(0).getSpecificClass().get();
			return Optional.of(mapValue(typeOfOptional, null, rawValue));
		}
		throw new IllegalStateException("Invalid data in the map: " + rawValue);
	}

	private Object mapValue(Class<?> declaredType, List<ParametrizedType> parametrizedTypes, Object value) {
		if (reflections.isSimple(declaredType)) return value;
		if (reflections.isAssignableTo(declaredType, MapEntity.class)) {
			//noinspection unchecked
			Map<String, Object> castedValue = (Map<String, Object>) value;
			//noinspection unchecked
			Class<? extends MapEntity> castedType = (Class<? extends MapEntity>) declaredType;
			return transform(castedValue, castedType);
		}
		if (reflections.isCollection(declaredType)) {
			List castedValue = (List) value;
			Class typeOfCollection = parametrizedTypes.get(0).getSpecificClass().get();
			//noinspection unchecked
			return castedValue.stream().map((old) -> mapValue(typeOfCollection, new ArrayList<>(), old)).collect(toList());
		}
		if (reflections.isAssignableTo(declaredType, Optional.class)) {
			Class typeOfOptional = parametrizedTypes.get(0).getSpecificClass().get();
			return Optional.of(mapValue(typeOfOptional, null, value));
		}
		throw new IllegalStateException("Invalid data in the map: " + value);
	}

	private void assertNoOptionalFieldIsSet(FieldAccessor accessor, Object rawValue) {
		if (!isOptional(accessor)) {
			if (rawValue == null) throw new IllegalStateException("Can't map the getter: " + accessor);
		}
	}

	private boolean isAGetter(FieldAccessor accessor) {
		return accessor.getType() == FieldAccessorType.GET;
	}

	private boolean isOptional(FieldAccessor accessor) {
		return accessor.getDeclaredType().equals(Optional.class);
	}

}
