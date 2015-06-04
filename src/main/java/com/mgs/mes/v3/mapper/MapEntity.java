package com.mgs.mes.v3.mapper;

import java.util.Map;

public interface MapEntity {
	Map<String, Object> asDomainMap();

	Map<String, Object> asValueMap();

	boolean dataEquals(MapEntity entity);
}
