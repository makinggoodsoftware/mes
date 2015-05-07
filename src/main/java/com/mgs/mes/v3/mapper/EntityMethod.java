package com.mgs.mes.v3.mapper;

import java.util.Map;

@FunctionalInterface
public interface EntityMethod <T extends MapEntity>{
	Object execute (Class<? extends T> type, T value, Map<String, Object> asMap, Object[] params);
}
