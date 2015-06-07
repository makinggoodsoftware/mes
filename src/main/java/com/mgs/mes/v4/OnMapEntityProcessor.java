package com.mgs.mes.v4;

import com.mgs.mes.v4.typeParser.ParsedType;

@FunctionalInterface
public interface OnMapEntityProcessor {
	Object process (ParsedType mapEntityParsedType, Object value);
}
