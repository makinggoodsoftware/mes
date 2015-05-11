package com.mgs.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.empty;

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

	public List<ParsedType> extractGenericClasses(Type genericReturnType) {
		List<ParsedType> empty = new ArrayList<>();
		if (genericReturnType == null) return empty;
		if (! (genericReturnType instanceof ParameterizedType)) return empty;

		ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

		if (actualTypeArguments == null || actualTypeArguments.length == 0) return empty;

		return Stream.of(actualTypeArguments).
				map(this::extractClass).
				collect(Collectors.toList());
	}

	public <T extends Annotation> Optional<T> annotation (Annotation[] annotations, Class<T> annotationType){
		for (Annotation annotation : annotations) {
			if (isAssignableTo(annotation.getClass(), annotationType))
				//noinspection unchecked
				return Optional.of((T) annotation);
		}
		return empty();
	}

	private ParsedType extractClass(Type actualTypeArgument) {
		String typeName = actualTypeArgument.getTypeName();
		try {
			Class<?> specificName = this.getClass().getClassLoader().loadClass(typeName);
//			return new ParsedType(typeName, of(specificName), empty());
			return null;
		} catch (ClassNotFoundException e) {
//			return new ParsedType(typeName, empty(), empty());
			return null;
		}
	}

	public boolean isCollection(Class<?> declaredType) {
		return isAssignableTo(declaredType, Collection.class);
	}
}
