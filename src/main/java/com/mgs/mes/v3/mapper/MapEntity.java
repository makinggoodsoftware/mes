package com.mgs.mes.v3.mapper;

import com.mgs.mes.v4.typeParser.ParsedType;
import com.mgs.mes.v5.VirtualField;

import java.util.Map;

public interface MapEntity {
	@VirtualField
	ParsedType getType();

	@VirtualField
	Map<String, Object> getDomainMap();

	@VirtualField
	Map<String, Object> getValueMap();

    @VirtualField
    boolean isMutable();

    boolean fieldsEquals(MapEntity entity);
}
