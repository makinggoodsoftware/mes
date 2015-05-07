package com.mgs.mes.v3.mapper;

import java.util.Map;
import java.util.UUID;

public class Retriever {
	public Map<String, Object> retrieveById(UUID refId) {
		return null;
	}

	Map<String, Object> asMap(UUID refId) {
		return retrieveById(refId);
	}
}
