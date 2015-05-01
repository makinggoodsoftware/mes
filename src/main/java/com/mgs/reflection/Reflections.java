package com.mgs.reflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class Reflections {
	private final static List<Class> PRIMITIVE_WRAPPERS = Arrays.asList(
			String.class,
			Double.class,
			Integer.class,
			Float.class,
			Date.class
	);

	public boolean isSimpleOrAssignableTo(Class<?> type, Class<?> assignableTo) {
		//noinspection SimplifiableIfStatement
		if (type.equals(void.class)) return false;

		return isSimple(type) || isAssignableTo(type, assignableTo);

	}

	public boolean isSimple(Class<?> type) {
		return type.isPrimitive() || PRIMITIVE_WRAPPERS.contains(type);
	}

	public boolean isAssignableTo(Class<?> type, Class<?> assignableTo) {
		return assignableTo.isAssignableFrom(type);
	}

	public List<ParametrizedType> extractGenericClasses(Type genericReturnType) {
		List<ParametrizedType> empty = new ArrayList<>();
		if (genericReturnType == null) return empty;
		if (! (genericReturnType instanceof ParameterizedType)) return empty;

		ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

		if (actualTypeArguments == null || actualTypeArguments.length == 0) return empty;

		return Stream.of(actualTypeArguments).
				map(this::extractClass).
				collect(Collectors.toList());
	}

	private ParametrizedType extractClass(Type actualTypeArgument) {
		String typeName = actualTypeArgument.getTypeName();
		try {
			Class<?> specificName = this.getClass().getClassLoader().loadClass(typeName);
			return new ParametrizedType(typeName, of(specificName));
		} catch (ClassNotFoundException e) {
			return new ParametrizedType(typeName, empty());
		}
	}

	public boolean isCollection(Class<?> declaredType) {
		return isAssignableTo(declaredType, Collection.class);
	}
}
