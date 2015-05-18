package com.mgs.reflection;

import com.mgs.mes.v3.reflection.GenericMethod;
import com.mgs.mes.v3.reflection.GenericMethods;
import com.mgs.mes.v3.reflection.GenericsExpert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.mgs.reflection.FieldAccessorType.BUILDER;
import static com.mgs.reflection.FieldAccessorType.GET;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toMap;

public class FieldAccessorParser {
	private static final String GET_PREFIX = "get";
	private static final String BUILDER_PREFIX = "with";
	private final BeanNamingExpert beanNamingExpert;
	private final GenericsExpert genericsExpert;

	public FieldAccessorParser(BeanNamingExpert beanNamingExpert, GenericsExpert genericsExpert) {
		this.beanNamingExpert = beanNamingExpert;
		this.genericsExpert = genericsExpert;
	}

	public Map<Method, Optional<FieldAccessor>> parseAll (Class clazz){
		return parseAll(genericsExpert.parseType(clazz));
	}

	public Map<Method, Optional<FieldAccessor>> parseAll (GenericType type){
		GenericMethods genericMethods = genericsExpert.parseMethods(type);
		Stream<Map.Entry<String, GenericMethod>> stream = genericMethods.getParsedMethodsAsMap().entrySet().stream();
		return stream.
				collect(toMap(
						(parsedMethodEntry) -> {
							GenericMethod value = parsedMethodEntry.getValue();
							return value.getMethod();
						},
						(parsedMethodEntry) -> parse(parsedMethodEntry.getValue())
				));
	}

	private Optional<FieldAccessor> parse(GenericMethod genericMethod) {
		if (isGetter(genericMethod)) {
			return parse(genericMethod, GET_PREFIX, GET, genericMethod.getMethod().getAnnotations());
		}

		if (isBuilder(genericMethod)) {
			return parse(genericMethod, BUILDER_PREFIX, BUILDER, genericMethod.getMethod().getAnnotations());
		}

		return empty();
	}

	public Stream<FieldAccessor> parse(Class type) {
		return 	parseAll(genericsExpert.parseType(type)).entrySet().stream().
				filter((methodToFieldAccessor) -> methodToFieldAccessor.getValue().isPresent()).
				map((methodToFieldAccessor) -> methodToFieldAccessor.getValue().get());
	}

	public Stream<FieldAccessor> parse(GenericType type) {
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
		GenericMethod genericMethod = genericsExpert.parseMethod(method);
		if (isGetter(genericMethod)) {
			return parse(genericMethod, GET_PREFIX, GET, method.getAnnotations());
		}

		if (isBuilder(genericMethod)) {
			return parse(genericMethod, BUILDER_PREFIX, BUILDER, method.getAnnotations());
		}

		return empty();
	}

	private boolean isGetter(GenericMethod genericMethod) {
		return
				genericMethod.getMethod().getName().indexOf(GET_PREFIX) == 0 &&
				genericMethod.getMethod().getParameterCount() == 0 &&
				genericMethod.getReturnType().getActualType().get() != void.class;
	}

	private boolean isBuilder(GenericMethod genericMethod) {
		return
				genericMethod.getMethod().getName().indexOf(BUILDER_PREFIX) == 0 &&
				genericMethod.getMethod().getParameterCount() == 1 &&
				genericMethod.getMethod().getDeclaringClass().equals(genericMethod.getReturnType().getActualType().get());
	}

	private Optional<FieldAccessor> parse(GenericMethod genericMethod, String prefix, FieldAccessorType type, Annotation[] annotations) {
		String methodName = genericMethod.getMethod().getName();
		String fieldName = beanNamingExpert.getFieldName(methodName, prefix);
		GenericType genericReturnType = genericMethod.getReturnType();
		return of(new FieldAccessor(methodName, fieldName, prefix, type, genericReturnType, annotations));
	}
}
