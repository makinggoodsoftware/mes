package com.mgs.mes.v3.reflection;

import com.mgs.reflection.GenericType;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;

public class ParsedTypeFactory {
	public GenericType unresolvedLeaf(Type type) {
		return new GenericType(empty(), type.getTypeName(), false, false, new HashMap<>());
	}

	public GenericType simpleType(Type type) {
		return new GenericType(Optional.of((Class)type), type.getTypeName(), true, false, new HashMap<>());
	}

	public GenericType parametrizedContainer(Class clazz, Type type, List<GenericType> genericTypes) {
		TypeVariable[] typeParameters = clazz.getTypeParameters();
		if (typeParameters.length != genericTypes.size()) throw new IllegalStateException();
		Map<Class, Map<String, GenericType>> genericTypesMap = new HashMap<>();
		Map<String, GenericType> genericTypesMapAux = new HashMap<>();
		for (int i=0;i<typeParameters.length;i++) {
			genericTypesMapAux.put(typeParameters[i].getName(), genericTypes.get(i));
		}
		genericTypesMap.put(clazz, genericTypesMapAux);
		return new GenericType(Optional.of(clazz), type.getTypeName(), true, true, genericTypesMap);
	}
}
