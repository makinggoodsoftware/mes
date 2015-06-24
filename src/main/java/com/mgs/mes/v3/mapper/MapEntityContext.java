package com.mgs.mes.v3.mapper;

import com.mgs.mes.v4.MapEntityFieldTransformer;
import com.mgs.mes.v4.MapWalker;
import com.mgs.mes.v4.typeParser.ParsedType;
import com.mgs.mes.v4.typeParser.TypeParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapEntityContext {
	private final ManagerLocator managerLocator;
	private final TypeParser typeParser;
	private final MapEntityFactory mapEntityFactory;
	private final MapWalker mapWalker;
	private final MapEntityFieldTransformer mapEntityFieldTransformer;

	public MapEntityContext(ManagerLocator managerLocator, TypeParser typeParser, MapEntityFactory mapEntityFactory, MapWalker mapWalker, MapEntityFieldTransformer mapEntityFieldTransformer) {
		this.managerLocator = managerLocator;
		this.typeParser = typeParser;
		this.mapEntityFactory = mapEntityFactory;
		this.mapWalker = mapWalker;
		this.mapEntityFieldTransformer = mapEntityFieldTransformer;
	}

	public <T extends MapEntity> T newEntity(Class<T> type, EntityMapBuilder<T> entityMapBuilder) {
		List<MapEntityManager<T>> mapEntityManagers = managerLocator.byType(type);
		return mapEntityFactory.newEntity(
				type,
				mapEntityManagers,
				entityMapBuilder
		);
	}

	public <T extends MapEntity> T transform(Map<String, Object> data, Class type) {
		return transform(data, typeParser.parse(type));
	}

	public <T extends MapEntity> T transform(Map<String, Object> valueMap, ParsedType type) {
		Map<String, Object> domainMap = new HashMap<>();
		mapWalker.walk(valueMap, type, (fieldAccessor, mapValue) -> {
			domainMap.put(
				fieldAccessor.getFieldName(),
				mapEntityFieldTransformer.transform(
					fieldAccessor.getReturnType(),
					mapValue,
					(mapEntityParsedType, value) -> {
						//noinspection unchecked
						Map<String, Object> castedValue = (Map<String, Object>) value;
						return transform(castedValue, mapEntityParsedType);
					}
				)
			);
		});

		Class actualType = type.getOwnDeclaration().getTypeResolution().getSpecificClass().get();
		//noinspection unchecked
		List<MapEntityManager<T>> entityManagers = managerLocator.byType(actualType);
		return mapEntityFactory.fromMap(type, entityManagers, domainMap, valueMap);
	}


}
