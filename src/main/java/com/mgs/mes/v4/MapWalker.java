package com.mgs.mes.v4;

import com.mgs.mes.v3.mapper.MapEntity;
import com.mgs.mes.v3.mapper.Mapping;
import com.mgs.mes.v4.typeParser.ParsedType;
import com.mgs.mes.v4.typeParser.TypeParser;
import com.mgs.reflection.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MapWalker {
	private final FieldAccessorParser fieldAccessorParser;
	private final BeanNamingExpert beanNamingExpert;
	private final Reflections reflections;
	private final TypeParser typeParser;

	public MapWalker(FieldAccessorParser fieldAccessorParser, BeanNamingExpert beanNamingExpert, Reflections reflections, TypeParser typeParser) {
		this.fieldAccessorParser = fieldAccessorParser;
		this.beanNamingExpert = beanNamingExpert;
		this.reflections = reflections;
		this.typeParser = typeParser;
	}

	public void walk(Map<String, Object> map, Class<? extends MapEntity> type, OnMapFieldCallback callback) {
		walk(map, typeParser.parse(type), callback);
	}

	public void walk(Map<String, Object> map, ParsedType type, OnMapFieldCallback callback) {
		Map<String, List<FieldAccessor>> accesorsByMethodName =
				fieldAccessorParser.parse(type).
						filter(this::isAGetter).
						collect(Collectors.groupingBy(FieldAccessor::getMethodName));

		accesorsByMethodName.entrySet().stream().forEach((accessorByMethodNameEntry) -> {
			Collection<FieldAccessor> accessors = accessorByMethodNameEntry.getValue();
			if (accessors.size() != 1) throw new IllegalStateException();

			FieldAccessor accessor = accessors.iterator().next();
			String fieldName = extractFieldName(accessor);
			callback.apply(accessor, map.get(fieldName));
		});
	}

	private String extractFieldName(FieldAccessor accessor) {
		Optional<Mapping> fieldNameOptional = reflections.annotation(accessor.getAnnotations(), Mapping.class);
		if (fieldNameOptional.isPresent()) return fieldNameOptional.get().mapFieldName();
		return beanNamingExpert.getFieldName(accessor.getMethodName(), "get");
	}

	private boolean isAGetter(FieldAccessor accessor) {
		return accessor.getType() == FieldAccessorType.GET;
	}
}
