package com.mgs.mes.v3.reflection;

import com.mgs.reflection.ParsedType;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;

public class ParsedTypeFactory {
	public ParsedType unresolvedLeaf(Type type) {
		return new ParsedType(empty(), type.getTypeName(), false, false, new HashMap<>());
	}

	public ParsedType specificLeaf(Class clazz, Type type) {
		return new ParsedType(Optional.of(clazz), type.getTypeName(), true, false, new HashMap<>());
	}

	public ParsedType parametrizedContainer(Class clazz, Type type, List<ParsedType> genericTypes) {
		TypeVariable[] typeParameters = clazz.getTypeParameters();
		if (typeParameters.length != genericTypes.size()) throw new IllegalStateException();
		Map<String, ParsedType> genericTypesMap = new HashMap<>();
		for (int i=0;i<typeParameters.length;i++) {
			genericTypesMap.put(typeParameters[i].getName(), genericTypes.get(i));
		}
		return new ParsedType(Optional.of(clazz), type.getTypeName(), true, true, genericTypesMap);
	}
}
