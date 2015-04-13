package com.mgs.reflection;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

		return type.isPrimitive() || PRIMITIVE_WRAPPERS.contains(type) || isAssignableTo(type, assignableTo);

	}

	public boolean isAssignableTo(Class<?> type, Class<?> assignableTo) {
		return assignableTo.isAssignableFrom(type);
	}
}
