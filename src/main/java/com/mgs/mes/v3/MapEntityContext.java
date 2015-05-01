package com.mgs.mes.v3;

import com.mgs.reflection.*;

import java.util.*;
import java.util.function.Function;

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

	public <T extends MapEntity> T transform(Map<String, Object> data, Class<T> type){
		Map<String, Object> domainMap = transformMap(data, (entry) -> mapEntry(type, entry));
		fieldAccessorParser.parse(type).
				filter(this::isAGetter).
				filter((getter) -> assertMandatoryGettersAreMapped(domainMap, getter, getter.getDeclaredType())).
				filter(this::isOptional).
				forEach((optionalGetter) -> {
					String methodName = optionalGetter.getMethodName();
					Object optionalGetterValue = domainMap.get(methodName);
					if (optionalGetterValue == null) domainMap.put(methodName, Optional.empty());
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

	private boolean isOptional(FieldAccessor getter) {
		return getter.getDeclaredType().equals(Optional.class);
	}

	private boolean isAGetter(FieldAccessor accessor) {
		return accessor.getType() == FieldAccessorType.GET;
	}

	private Map<String, Object> transformMap(
			Map<String, Object> data,
			Function<Map.Entry<String, Object>, Map.Entry<String, Object>> mapTransformation
	) {
		Map<String,Object> domainValues = new HashMap<>();
		data.entrySet().stream().map(mapTransformation).forEach((entry)->domainValues.put(entry.getKey(), entry.getValue()));
		return domainValues;
	}

	private Map.Entry<String, Object> mapEntry(Class<? extends MapEntity> parentType, Map.Entry<String, Object> entry) {
		String fieldName = entry.getKey();
		String getterName = beanNamingExpert.getGetterName(fieldName);
		FieldAccessor fieldAccessor = fieldAccessorParser.parse(parentType, getterName).get();
		Object mappedValue = mapValue (fieldAccessor.getDeclaredType(), fieldAccessor.getParametrizedTypes(), entry.getValue());
		return new AbstractMap.SimpleEntry<>(getterName, mappedValue);
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
			return castedValue.stream().map((old)->mapValue(typeOfCollection, new ArrayList<>(), old)).collect(toList());
		}
		if (reflections.isAssignableTo(declaredType, Optional.class)) {
			Class typeOfOptional = parametrizedTypes.get(0).getSpecificClass().get();
			return Optional.of(mapValue(typeOfOptional, null, value));
		}
		throw new IllegalStateException("Invalid data in the map: " + value);
	}

	private boolean assertMandatoryGettersAreMapped(Map<String, Object> domainValues, FieldAccessor getter, Class type) {
		if (type.equals(Optional.class)) return true;

		String methodName = getter.getMethodName();
		Object domainValue = domainValues.get(methodName);
		if (domainValue == null) throw new IllegalStateException("Can't map the getter: " + methodName + " in: "+ domainValues);
		return true;
	}
}
