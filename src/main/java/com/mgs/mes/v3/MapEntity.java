package com.mgs.mes.v3;

import java.util.Map;

public interface MapEntity {
	Map<String, Object> asMap ();
	boolean dataEquals(MapEntity entity);
}
