package com.mgs.mes.v3.reflection;

import com.mgs.reflection.ParsedType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

public class ParsedTypeFactory {
	public ParsedType unresolvedLeaf(Type type) {
		return new ParsedType(empty(), type.getTypeName(), false, false, new ArrayList<>());
	}

	public ParsedType specificLeaf(Class clazz, Type type) {
		return new ParsedType(Optional.of(clazz), type.getTypeName(), true, false, new ArrayList<>());
	}

	public ParsedType parametrizedContainer(Class clazz, Type type, List<ParsedType> genericTypes) {
		return new ParsedType(Optional.of(clazz), type.getTypeName(), true, true, genericTypes);
	}
}
