package com.mgs.reflection;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static com.mgs.reflection.FieldAccessorType.BUILDER;
import static com.mgs.reflection.FieldAccessorType.GET;
import static java.util.Optional.empty;

public class FieldAccessorParser {
	private static final String GET_PREFIX = "get";
	private static final String BUILDER_PREFIX = "with";
	private final BeanNamingExpert beanNamingExpert;

	public FieldAccessorParser(BeanNamingExpert beanNamingExpert) {
		this.beanNamingExpert = beanNamingExpert;
	}

	public Stream<Optional<FieldAccessor>> parseAll (Class type){
		return Arrays.asList(type.getDeclaredMethods()).stream().
				map(this::parse);
	}

	public Stream<FieldAccessor> parse(Class type) {
		return 	parseAll(type).
				filter(Optional::isPresent).
				map(Optional::get);
	}

	public Optional<FieldAccessor> parse(Class<?> type, String accessorName) {
		try {
			return parse(type.getMethod(accessorName));
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException();
		}
	}

	public Optional<FieldAccessor> parse(Method method) {
		if (isGetter(method)) {
			return parse(method.getReturnType(), method.getName(), GET_PREFIX, GET);
		}

		if (isBuilder(method)) {
			return parse(method.getParameters()[0].getType(), method.getName(), BUILDER_PREFIX, BUILDER);
		}

		return empty();
	}

	private boolean isGetter(Method method) {
		return
				method.getName().indexOf(GET_PREFIX) == 0 &&
				method.getParameterCount() == 0 &&
				method.getReturnType() != void.class;
	}

	private boolean isBuilder(Method method) {
		return
				method.getName().indexOf(BUILDER_PREFIX) == 0 &&
				method.getParameterCount() == 1 &&
				method.getDeclaringClass().equals(method.getReturnType());
	}

	private Optional<FieldAccessor> parse(Class<?> declaredType, String methodName, String prefix, FieldAccessorType type) {
		String fieldName = beanNamingExpert.getFieldName(methodName, prefix);
		return Optional.of(new FieldAccessor(declaredType, methodName, fieldName, prefix, type));
	}
}
