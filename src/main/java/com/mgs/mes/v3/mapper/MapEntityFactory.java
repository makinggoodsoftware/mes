package com.mgs.mes.v3.mapper;

import com.google.common.collect.ImmutableMap;
import com.mgs.mes.v4.MapValueProcessor;
import com.mgs.mes.v4.MapWalker;
import com.mgs.mes.v4.typeParser.Declaration;
import com.mgs.mes.v4.typeParser.ParsedType;
import com.mgs.mes.v4.typeParser.TypeParser;
import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.FieldAccessorParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.stream.Collectors.toMap;

public class MapEntityFactory {
	private final TypeParser typeParser;
	private final FieldAccessorParser fieldAccessorParser;
	private final MapWalker mapWalker;
	private final MapValueProcessor mapValueProcessor;

	public MapEntityFactory(TypeParser typeParser, FieldAccessorParser fieldAccessorParser, MapWalker mapWalker, MapValueProcessor mapValueProcessor) {
		this.typeParser = typeParser;
		this.fieldAccessorParser = fieldAccessorParser;
		this.mapWalker = mapWalker;
		this.mapValueProcessor = mapValueProcessor;
	}

	public <T extends MapEntity> T newEntity(
			Class type,
			List<MapEntityManager<T>> entityManagers,
			EntityMapBuilder<T> entityMapBuilder
	) {
		ParsedType parsedType = typeParser.parse(type);
		return newEntity(parsedType, entityManagers, entityMapBuilder);
	}

	private <T extends MapEntity> T newEntity(Declaration entityType, List<MapEntityManager<T>> entityManagers, EntityMapBuilder<T> entityMapBuilder) {
		ParsedType parsedType = typeParser.parse(entityType);
		return newEntity(parsedType, entityManagers, entityMapBuilder);
	}

	public <T extends MapEntity> T newEntity(
			ParsedType parsedType,
			List<MapEntityManager<T>> entityManagers,
			EntityMapBuilder<T> entityMapBuilder
	) {
		Map<String, FieldAccessor> fieldAccessors = accessors(parsedType);
		return newEntity(parsedType, fieldAccessors, entityManagers, entityMapBuilder);
	}

	public <T extends MapEntity> T newEntity(
			ParsedType parsedType,
			Map<String, FieldAccessor> fieldAccessors,
			List<MapEntityManager<T>> entityManagers,
			EntityMapBuilder<T> entityMapBuilder
	) {
		Class actualType = parsedType.getOwnDeclaration().getTypeResolution().getSpecificClass().get();
		T emptyEntity = create(
				parsedType,
				actualType,
				entityManagers,
				fieldAccessors,
				new HashMap<>(),
				new HashMap<>(),
				true
		);
		T modifiableEntity = entityMapBuilder.apply(emptyEntity);
		Map<String, Object> domainMap = modifiableEntity.asDomainMap();
		return create(
				parsedType,
				actualType,
				entityManagers,
				fieldAccessors,
				ImmutableMap.copyOf(domainMap),
				valueMap(domainMap, parsedType),
				false
		);
	}

	private <T extends MapEntity> T create(
			ParsedType parsedType,
			Class actualType,
			List<MapEntityManager<T>> entityManagers,
			Map<String, FieldAccessor> fieldAccessors,
			Map<String, Object> domainMap,
			Map<String, Object> valueMap,
			boolean modifiable
	) {
		//noinspection unchecked
		return (T) newProxyInstance(
				MapEntityContext.class.getClassLoader(),
				new Class[]{actualType},
				new EntityMapCallInterceptor<>(
						this,
						parsedType,
						domainMap,
						valueMap,
						entityManagers,
						fieldAccessors,
						modifiable,
						(newEntityType, builder) -> newEntity(newEntityType, entityManagers, builder)
				)
		);
	}

	public <T extends MapEntity> Object fromMap(
			ParsedType type,
			Map<String, FieldAccessor> fieldAccessors,
			List<MapEntityManager<T>> entityManagers,
			Map<String, Object> domainMap
	) {
		return fromMap(type, fieldAccessors, entityManagers, domainMap, valueMap(domainMap, type));
	}

	private Map<String, Object> valueMap(Map<String, Object> domainMap, ParsedType type) {
		Map<String, Object> valueMap = new HashMap<>();
		mapWalker.walk(domainMap, type, (fieldAccessor, mapValue) -> {
			valueMap.put(
					fieldAccessor.getFieldName(),
					mapValueProcessor.transform(
							fieldAccessor.getReturnType(),
							mapValue,
							(mapEntityParsedType, value) -> {
								//noinspection unchecked
								MapEntity castedValue = (MapEntity) value;
								return valueMap(castedValue.asDomainMap(), mapEntityParsedType);
							}
					)
			);
		});
		return valueMap;
	}

	public <T extends MapEntity> T fromMap(
			Class type,
			List<MapEntityManager<T>> entityManagers,
			Map<String, Object> domainMap,
			Map<String, Object> valueMap
	) {
		ParsedType parsedType = typeParser.parse(type);
		return fromMap(parsedType, entityManagers, domainMap, valueMap);
	}

	public <T extends MapEntity> T fromMap(
			ParsedType parsedType,
			List<MapEntityManager<T>> entityManagers,
			Map<String, Object> domainMap,
			Map<String, Object> valueMap
	) {
		Map<String, FieldAccessor> fieldAccessors = accessors(parsedType);
		return fromMap(parsedType, fieldAccessors, entityManagers, domainMap, valueMap);
	}

	public <T extends MapEntity> T fromMap(
			ParsedType parsedType,
			Map<String, FieldAccessor> fieldAccessors,
			List<MapEntityManager<T>> entityManagers,
			Map<String, Object> domainMap,
			Map<String, Object> valueMap
	) {
		Class actualType = parsedType.getOwnDeclaration().getTypeResolution().getSpecificClass().get();

		return create(
				parsedType,
				actualType,
				entityManagers,
				fieldAccessors,
				domainMap,
				valueMap,
				false
		);
	}

	private Map<String, FieldAccessor> accessors(ParsedType parsedType) {
		return fieldAccessorParser.parse(parsedType).collect(toMap(
						FieldAccessor::getMethodName,
						(parsedMethod) -> parsedMethod)
		);
	}
}
