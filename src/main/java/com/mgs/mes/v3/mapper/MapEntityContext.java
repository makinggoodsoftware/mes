package com.mgs.mes.v3.mapper;

import com.mgs.mes.v3.reflection.GenericsExpert;
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
	private final GenericsExpert genericsExpert;

	public MapEntityContext(ManagerLocator managerLocator, FieldAccessorParser fieldAccessorParser, BeanNamingExpert beanNamingExpert, Reflections reflections, GenericsExpert genericsExpert) {
		this.managerLocator = managerLocator;
		this.fieldAccessorParser = fieldAccessorParser;
		this.beanNamingExpert = beanNamingExpert;
		this.reflections = reflections;
		this.genericsExpert = genericsExpert;
	}

	public <T extends MapEntity> T transform(Map<String, Object> data, Class type) {
		return transform(data, genericsExpert.parseType(type));
	}

	public <T extends MapEntity> T transform(Map<String, Object> data, GenericType type) {
		Map<String, Object> domainMap = new HashMap<>();
		Map<String, List<FieldAccessor>> accesorsByMethodName = fieldAccessorParser.parse(type).
				filter(this::isAGetter).
				collect(Collectors.groupingBy(FieldAccessor::getMethodName));

		accesorsByMethodName.entrySet().stream().forEach((accessorByMethodNameEntry) -> {
			Collection<FieldAccessor> accessors = accessorByMethodNameEntry.getValue();
			if (accessors.size() != 1) throw new IllegalStateException();

			FieldAccessor accessor = accessors.iterator().next();
			String fieldName = extractFieldName(accessor);
			Object value = domainValue(accessor.getReturnType(), data.get(fieldName));
			domainMap.put(accessor.getMethodName(), value);
		});

		Class actualType = type.getActualType().get();
		//noinspection unchecked
		return (T) newProxyInstance(
				MapEntityContext.class.getClassLoader(),
				new Class[]{actualType},
				new EntityMapCallInterceptor<>(
						type,
						domainMap,
						managerLocator.byType(actualType)
				)
		);
	}

	private String extractFieldName(FieldAccessor accessor) {
		Optional<Mapping> fieldNameOptional = reflections.annotation(accessor.getAnnotations(), Mapping.class);
		if (fieldNameOptional.isPresent()) return fieldNameOptional.get().mapFieldName();
		return beanNamingExpert.getFieldName(accessor.getMethodName(), "get");
	}

	private Object domainValue(GenericType returnType, Object rawValue) {
		assertNoOptionalFieldIsSet(returnType, rawValue);

		Class<?> declaredType = returnType.getActualType().get();
		if (reflections.isSimple(declaredType)) return rawValue;
		if (reflections.isAssignableTo(declaredType, MapEntity.class)) {
			//noinspection unchecked
			Map<String, Object> castedValue = (Map<String, Object>) rawValue;
			return transform(
					castedValue,
					returnType
			);
		}
		if (reflections.isCollection(declaredType)) {
			List castedValue = (List) rawValue;
			GenericType typeOfCollection = returnType.getParameters().get(declaredType).values().iterator().next();
			//noinspection unchecked
			return castedValue.stream().map((old) -> mapValue(typeOfCollection, old)).collect(toList());
		}
		if (reflections.isAssignableTo(declaredType, Optional.class)) {
			if (rawValue == null) return Optional.empty();
			GenericType typeOfOptional = returnType.getParameters().get(declaredType).values().iterator().next();
			return Optional.of(mapValue(typeOfOptional, rawValue));
		}
		throw new IllegalStateException("Invalid data in the map: " + rawValue);
	}

	private Object mapValue(GenericType genericType, Object value) {
//		Class declaredType = genericType.getActualType().get();
//		if (reflections.isSimple(declaredType)) return value;
//		if (reflections.isAssignableTo(declaredType, MapEntity.class)) {
//			//noinspection unchecked
//			Map<String, Object> castedValue = (Map<String, Object>) value;
//			//noinspection unchecked
//			return transform(castedValue, genericType);
//		}
//		if (reflections.isCollection(declaredType)) {
//			List castedValue = (List) value;
//			//noinspection unchecked
//			return castedValue.stream().map((old) -> mapValue(null, old)).collect(toList());
//		}
//		if (reflections.isAssignableTo(declaredType, Optional.class)) {
//			Class typeOfOptional = null;
//			return Optional.of(mapValue(null, value));
//		}
//		throw new IllegalStateException("Invalid data in the map: " + value);
		return domainValue(genericType, value);
	}

	private void assertNoOptionalFieldIsSet(GenericType returnType, Object rawValue) {
		if (!isOptional(returnType)) {
			if (rawValue == null) throw new IllegalStateException("Can't map the getter: " + returnType);
		}
	}

	private boolean isAGetter(FieldAccessor accessor) {
		return accessor.getType() == FieldAccessorType.GET;
	}

	private boolean isOptional(GenericType returnType) {
		return returnType.getActualType().get().equals(Optional.class);
	}

}
