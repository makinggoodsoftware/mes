package com.mgs.mes.v5;

import com.mgs.mes.v3.mapper.MapEntity;
import com.mgs.mes.v3.mapper.MapEntityContext;
import com.mgs.mes.v4.typeParser.ParsedType;
import com.mgs.mes.v4.typeParser.TypeParser;

import java.util.List;
import java.util.Map;

import static java.lang.reflect.Proxy.newProxyInstance;

public class MapEntityFactory {
	private final TypeParser typeParser;
	private final MapEntityMethodLinker mapEntityMethodLinker;
	private final List<Object> managers;
	private final MapEntityTransformer mapEntityTransformer;
	private final MapManager mapManager;

	public MapEntityFactory(TypeParser typeParser, MapEntityMethodLinker mapEntityMethodLinker, List<Object> managers, MapEntityTransformer mapEntityTransformer, MapManager mapManager) {
		this.typeParser = typeParser;
		this.mapEntityMethodLinker = mapEntityMethodLinker;
		this.managers = managers;
		this.mapEntityTransformer = mapEntityTransformer;
		this.mapManager = mapManager;
	}

	public <T extends MapEntity> T immutable(Class<T> type, Map<String, Object> valueMap) {
		ParsedType parsedType = typeParser.parse(type);
		return immutable(parsedType, valueMap);
	}

	public <T extends MapEntity> T immutable(ParsedType type, Map<String, Object> valueMap) {
		return fromValueMap(type, valueMap, false);
	}

	public MapEntity mutable(MapEntity mapEntity) {
		return ofMutability(mapEntity, true);
	}

	public MapEntity immutable(MapEntity mapEntity) {
		return ofMutability(mapEntity, false);
	}

	private MapEntity ofMutability(MapEntity mapEntity, boolean mutable) {
		if (mapEntity.isMutable() == mutable) return mapEntity;
		return proxy(mutable, mapEntity.getType().getActualType().get(), mapEntity.getValueMap(), mapEntity.getDomainMap());
	}

	private <T extends MapEntity> T fromValueMap(ParsedType type, Map<String, Object> valueMap, boolean mutable) {
		Class actualType = type.getActualType().get();
		Map<String, Object> domainMap = mapEntityTransformer.transform(type, valueMap, (mapEntityParsedType, value) -> {
			//noinspection unchecked
			Map<String, Object> castedValue = (Map<String, Object>) value;
			return fromValueMap(mapEntityParsedType, castedValue, mutable);
		});
		return proxy(mutable, actualType, valueMap, domainMap);

	}

	private <T extends MapEntity> T proxy(boolean mutable, Class actualType, Map<String, Object> valueMap, Map<String, Object> domainMap) {
		//noinspection unchecked
		return (T) newProxyInstance(
				MapEntityContext.class.getClassLoader(),
				new Class[]{actualType},
				new MapEntityProxy(
						actualType,
						mutable ? mapManager.mutable(domainMap) : mapManager.immutable(domainMap),
						mutable ? mapManager.mutable(valueMap) : mapManager.immutable(valueMap),
						mapEntityMethodLinker.link(actualType, managers),
						mutable
				)
		);
	}
}
