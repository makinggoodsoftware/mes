package com.mgs.mes.v3.mapper;

import com.mgs.mes.v4.typeParser.Declaration;
import com.mgs.mes.v4.typeParser.ParsedType;
import com.mgs.mes.v4.typeParser.TypeParser;
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
	private final TypeParser typeParser;

	public MapEntityContext(ManagerLocator managerLocator, FieldAccessorParser fieldAccessorParser, BeanNamingExpert beanNamingExpert, Reflections reflections, TypeParser typeParser) {
		this.managerLocator = managerLocator;
		this.fieldAccessorParser = fieldAccessorParser;
		this.beanNamingExpert = beanNamingExpert;
		this.reflections = reflections;
		this.typeParser = typeParser;
	}

	public <T extends MapEntity> T transform(Map<String, Object> data, Class type) {
		return transform(data, typeParser.parse(type));
	}

	public <T extends MapEntity> T transform(Map<String, Object> data, ParsedType type) {
		Map<String, Object> domainMap = new HashMap<>();
		Map<String, List<FieldAccessor>> accesorsByMethodName =
				fieldAccessorParser.parse(type).
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

		Class actualType = type.getOwnDeclaration().getTypeResolution().getSpecificClass().get();
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

	private Object domainValue(ParsedType parsedType, Object rawValue) {
		assertParsedTypeIsResolved(parsedType);
		assertNoOptionalFieldIsSet(parsedType, rawValue);

		Class<?> declaredType = parsedType.getActualType().get();
		if (reflections.isSimple(declaredType)) return rawValue;
		if (reflections.isAssignableTo(declaredType, MapEntity.class)) {
			//noinspection unchecked
			Map<String, Object> castedValue = (Map<String, Object>) rawValue;
			return transform(
					castedValue,
					parsedType
			);
		}
		if (reflections.isCollection(declaredType)) {
			List castedValue = (List) rawValue;
			Declaration typeOfCollection = parsedType.getOwnDeclaration().getParameters().values().iterator().next();
			//noinspection unchecked
			return castedValue.stream().map((old) ->
					domainValue(typeParser.parse(typeOfCollection), old)).collect(toList()
			);
		}
		if (reflections.isAssignableTo(declaredType, Optional.class)) {
			if (rawValue == null) return Optional.empty();
			Declaration typeOfOptional = parsedType.getOwnDeclaration().getParameters().values().iterator().next();
			return Optional.of(domainValue(typeParser.parse(typeOfOptional), rawValue));
		}
		throw new IllegalStateException("Invalid data in the map: " + rawValue);
	}

	private void assertParsedTypeIsResolved(ParsedType returnType) {
		if (! returnType.getActualType().isPresent()) {
			throw new IllegalStateException("Can't map into a type which is not resolved");
		}
	}

	private void assertNoOptionalFieldIsSet(ParsedType returnType, Object rawValue) {
		if (!isOptional(returnType)) {
			if (rawValue == null) throw new IllegalStateException("Can't map the getter: " + returnType);
		}
	}

	private boolean isAGetter(FieldAccessor accessor) {
		return accessor.getType() == FieldAccessorType.GET;
	}

	private boolean isOptional(ParsedType parsedType) {
		Optional<Class> actualType = parsedType.getActualType();
		return actualType.isPresent() && actualType.get().equals(Optional.class);
	}

}
