package com.mgs.mes.v5;

import com.mgs.mes.v3.mapper.MapEntity;
import com.mgs.mes.v4.MapEntityFieldTransformer;
import com.mgs.mes.v4.typeParser.ParsedType;

public class MapEntityManager {
	private final MapEntityFactory mapEntityFactory;
	private final MapEntityFieldTransformer mapEntityFieldTransformer;

	public MapEntityManager(MapEntityFactory mapEntityFactory, MapEntityFieldTransformer mapEntityFieldTransformer) {
		this.mapEntityFactory = mapEntityFactory;
		this.mapEntityFieldTransformer = mapEntityFieldTransformer;
	}

	@MapEntityMethod(pattern= "get{fieldName}")
	public Object onFieldGet (
			MapEntity mapEntity,
			@PatternReader(extract ="fieldName")String fieldName
	){
		return mapEntity.getDomainMap().get(fieldName);
	}

	@MapEntityMethod(pattern= "with{fieldName}")
	public MapEntity onEntityWith (
			MapEntityProxy mapEntityProxy,
			MapEntity mapEntity,
			ParsedType parsedType,
			@PatternReader(extract ="fieldName")String fieldName,
			@MethodParameters Object value
	){
		MapEntity mutableEntity = mapEntityFactory.mutable(mapEntity);
		mutableEntity.getDomainMap().put(fieldName, value);
		mutableEntity.getValueMap().put(
				fieldName,
				mapEntityFieldTransformer.transform(parsedType, value, (mapEntityParsedType, mapEntityFieldValue) -> {
					MapEntity castedValue = (MapEntity) mapEntityFieldValue;
					return castedValue.getValueMap();
				})

		);
		return mapEntityProxy.isMutable() ?
				mutableEntity :
				mapEntityFactory.immutable(mutableEntity);
	}

}
