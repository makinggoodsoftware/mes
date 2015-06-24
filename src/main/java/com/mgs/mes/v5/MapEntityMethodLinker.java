package com.mgs.mes.v5;

import com.mgs.mes.v4.typeParser.ParsedType;
import com.mgs.mes.v4.typeParser.TypeParser;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class MapEntityMethodLinker {
	private final TypeParser typeParser;

    public MapEntityMethodLinker(TypeParser typeParser) {
        this.typeParser = typeParser;
    }

    public <T> Map<Method, MapEntityMethodManager> link(Class<T> type, List<Object> managers) {
        ParsedType parsedType = typeParser.parse(type);

        return null;
	}
}
