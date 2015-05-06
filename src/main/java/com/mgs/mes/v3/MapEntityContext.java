package com.mgs.mes.v3;

import com.mgs.reflection.*;

import java.util.*;
import java.util.stream.Collectors;

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
		return doTransform(data, type, null);
	}

	private <T extends MapEntity> T doTransform(Map<String, Object> data, Class<T> type, List<Class> parametrizedTypes) {
		Map<String, Object> domainMap = new HashMap<>();
		Map<String, List<FieldAccessor>> bridgeMethodsByMethodName = fieldAccessorParser.parse(type).
				filter(this::isAGetter).
				collect(Collectors.groupingBy(FieldAccessor::getMethodName));

		bridgeMethodsByMethodName.entrySet().stream().forEach((bridgeMethodsEntry) -> {
			Collection<FieldAccessor> bridgeMethods = bridgeMethodsEntry.getValue();

			FieldAccessor accessor;

			if (bridgeMethods.size() == 1) {
				accessor = bridgeMethods.iterator().next();
			} else if (bridgeMethods.size() == 2) {
				accessor = bridgeMethods.stream().
						filter((fieldAccessor) ->
								reflections.annotation(fieldAccessor.getAnnotations(), Parametrized.class).isPresent()).
						findFirst().get();
			} else {
				throw new IllegalStateException();
			}

			String fieldName = extractFieldName(accessor);
			Object value = domainValue(accessor, data.get(fieldName), parametrizedTypes);
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

	private String extractFieldName(FieldAccessor accessor) {
		Optional<Mapping> fieldNameOptional = reflections.annotation(accessor.getAnnotations(), Mapping.class);
		if (fieldNameOptional.isPresent()) return fieldNameOptional.get().mapFieldName();
		return beanNamingExpert.getFieldName(accessor.getMethodName(), "get");
	}

	private Object domainValue(FieldAccessor accessor, Object rawValue, List<Class> parentParametrizedTypes) {
		assertNoOptionalFieldIsSet(accessor, rawValue);

		Class<?> declaredType = extractDeclaredType(accessor, parentParametrizedTypes);
		List<ParametrizedType> parametrizedTypes = accessor.getParametrizedTypes();
		if (reflections.isSimple(declaredType)) return rawValue;
		if (reflections.isAssignableTo(declaredType, MapEntity.class)) {
			//noinspection unchecked
			Map<String, Object> castedValue = (Map<String, Object>) rawValue;
			//noinspection unchecked
			Class<? extends MapEntity> castedType = (Class<? extends MapEntity>) declaredType;
			return doTransform(
					castedValue,
					castedType,
					parametrizedTypes.stream().
							filter((parametrizedType) -> parametrizedType.getSpecificClass().isPresent()).
							map((parametrizedType) -> parametrizedType.getSpecificClass().get()).
							collect(toList())
			);
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

	private Class<?> extractDeclaredType(FieldAccessor accessor, List<Class> parentParametrizedTypes) {
		Optional<Parametrizable> parametrizable = reflections.annotation(accessor.getAnnotations(), Parametrizable.class);
		if (parametrizable.isPresent()){
			if (parentParametrizedTypes == null || parentParametrizedTypes.size() != 1) throw new IllegalStateException();
			return parentParametrizedTypes.get(0);
		}

		return accessor.getDeclaredType();
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
