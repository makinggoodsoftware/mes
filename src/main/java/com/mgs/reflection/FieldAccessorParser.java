package com.mgs.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.mgs.reflection.FieldAccessorType.BUILDER;
import static com.mgs.reflection.FieldAccessorType.GET;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toMap;

public class FieldAccessorParser {
	private static final String GET_PREFIX = "get";
	private static final String BUILDER_PREFIX = "with";
	private final BeanNamingExpert beanNamingExpert;
	private final Reflections reflections;

	public FieldAccessorParser(BeanNamingExpert beanNamingExpert, Reflections reflections) {
		this.beanNamingExpert = beanNamingExpert;
		this.reflections = reflections;
	}

	public Map<Method, Optional<FieldAccessor>> parseAll (Class type){
		return asList(type.getMethods()).stream().
				collect(toMap(
						(method) -> method,
						this::parse
				));
	}

	public Stream<FieldAccessor> parse(Class type) {
		return 	parseAll(type).entrySet().stream().
				filter((methodToFieldAccessor) -> methodToFieldAccessor.getValue().isPresent()).
				map((methodToFieldAccessor) -> methodToFieldAccessor.getValue().get());
	}

	public Optional<FieldAccessor> parse(Class<?> type, String accessorName) {
		try {
			return parse(type.getMethod(accessorName));
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Can't find the accessor with name: " + accessorName + " in the type  " + type);
		}
	}

	public Optional<FieldAccessor> parse(Method method) {
		if (isGetter(method)) {
			List<ParametrizedType> parametrizedType = reflections.extractGenericClasses(method.getGenericReturnType());
			return parse(method.getReturnType(), method.getName(), GET_PREFIX, GET, parametrizedType, method.getAnnotations(), method.isBridge());
		}

		if (isBuilder(method)) {
			List<ParametrizedType> parametrizedType  = reflections.extractGenericClasses(method.getGenericParameterTypes()[0]);
			return parse(method.getParameters()[0].getType(), method.getName(), BUILDER_PREFIX, BUILDER, parametrizedType, method.getAnnotations(), method.isBridge());
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

	private Optional<FieldAccessor> parse(Class<?> declaredType, String methodName, String prefix, FieldAccessorType type, List<ParametrizedType> parametrizedTypes, Annotation[] annotations, Boolean isBridge) {
		String fieldName = beanNamingExpert.getFieldName(methodName, prefix);
		return of(new FieldAccessor(declaredType, methodName, fieldName, prefix, type, parametrizedTypes, annotations, isBridge));
	}
}
