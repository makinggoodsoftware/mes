package com.mgs.mes.v3.reflection;

import com.mgs.reflection.ParametrizedType;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public class GenericsExpert {
	public ParametrizedType parseMethodReturnType(Method toParse) {
		Type genericReturnType = toParse.getGenericReturnType();
		Optional<Class> specificClass = of(toParse.getReturnType());
		String name = specificClass.get().getName();
		Optional<List<ParametrizedType>> childrenParametrizedTyped = childrenParametrizedTypes(genericReturnType);
		return new ParametrizedType(name, specificClass, childrenParametrizedTyped);

	}

	private Optional<List<ParametrizedType>> childrenParametrizedTypes(Type genericReturnType) {
		if (genericReturnType == null) return empty();
		if (!(genericReturnType instanceof ParameterizedType)) return empty();

		ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

		if (actualTypeArguments == null || actualTypeArguments.length == 0) return empty();

		return of(Stream.of(actualTypeArguments).
				map((actualTypeArgument) -> extractClass(actualTypeArgument.getTypeName())).
				collect(Collectors.toList()));
	}

	private ParametrizedType extractClass(String typeName) {
		int genericsStartPosition = typeName.indexOf("<");
		return genericsStartPosition < 0 ? createParametrizedType(typeName, empty()) : genericType(typeName, genericsStartPosition);
	}

	private ParametrizedType genericType(String typeName, int genericsStartPosition) {
		try {
			String thisTypeName = typeName.substring(0, genericsStartPosition);
			String remainder = typeName.substring(genericsStartPosition + 1, typeName.length() - 1);
			Class<?> specificClass = this.getClass().getClassLoader().loadClass(thisTypeName);
			Optional<List<ParametrizedType>> childrenParametrizedTypes = of(singletonList(extractClass(remainder)));
			return new ParametrizedType(typeName, of(specificClass), childrenParametrizedTypes);
		} catch (ClassNotFoundException e) {
			return new ParametrizedType(typeName, empty(), empty());
		}
	}

	private ParametrizedType createParametrizedType(String typeName, Optional<List<ParametrizedType>> childrenParametrizedTypes) {
		try {
			Class<?> specificClass = this.getClass().getClassLoader().loadClass(typeName);
			return new ParametrizedType(typeName, of(specificClass), childrenParametrizedTypes);
		} catch (ClassNotFoundException e) {
			return new ParametrizedType(typeName, empty(), childrenParametrizedTypes);
		}
	}
}
