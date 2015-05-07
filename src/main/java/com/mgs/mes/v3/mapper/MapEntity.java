package com.mgs.mes.v3.mapper;

import java.util.Map;

public interface MapEntity {
	Map<String, Object> asMap ();
	boolean dataEquals(MapEntity entity);
}
