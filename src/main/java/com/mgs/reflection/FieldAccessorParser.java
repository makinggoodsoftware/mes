package com.mgs.reflection;

import com.mgs.mes.v3.reflection.GenericMethod;
import com.mgs.mes.v3.reflection.GenericMethods;
import com.mgs.mes.v4.typeParser.ParsedType;
import com.mgs.mes.v4.typeParser.TypeParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
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
	private final TypeParser typeParser;

	public FieldAccessorParser(BeanNamingExpert beanNamingExpert, TypeParser typeParser) {
		this.beanNamingExpert = beanNamingExpert;
		this.typeParser = typeParser;
	}

	public Map<Method, Optional<FieldAccessor>> parseAll(Class clazz) {
		return parseAll(typeParser.parse(clazz));
	}

	public Map<Method, Optional<FieldAccessor>> parseAll(ParsedType type) {
		GenericMethods genericMethods = typeParser.parseMethods(type);
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
		return parseAll(typeParser.parse(type)).entrySet().stream().
				filter((methodToFieldAccessor) -> methodToFieldAccessor.getValue().isPresent()).
				map((methodToFieldAccessor) -> methodToFieldAccessor.getValue().get());
	}

	public Stream<FieldAccessor> parse(ParsedType type) {
		return parseAll(type).entrySet().stream().
				filter((methodToFieldAccessor) -> methodToFieldAccessor.getValue().isPresent()).
				map((methodToFieldAccessor) -> methodToFieldAccessor.getValue().get());
	}

//	public Optional<FieldAccessor> parse(Class<?> type, String accessorName) {
//		return parse(type, accessorName);
//	}

	public Optional<FieldAccessor> parse(Class from, String methodName, Class... parameters) {
		try {
			ParsedType parsedType = typeParser.parse(from);
			GenericMethod genericMethod = typeParser.parseMethod(parsedType, from.getMethod(methodName, parameters));
			if (isGetter(genericMethod)) {
				return parse(genericMethod, GET_PREFIX, GET, genericMethod.getMethod().getAnnotations());
			}

			if (isBuilder(genericMethod)) {
				return parse(genericMethod, BUILDER_PREFIX, BUILDER, genericMethod.getMethod().getAnnotations());
			}

			return empty();
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}
	}

	private boolean isGetter(GenericMethod genericMethod) {
		Optional<Class> actualType = genericMethod.getReturnType().getOwnDeclaration().getTypeResolution().getSpecificClass();
		if (!actualType.isPresent()) {
			throw new IllegalStateException();
		}
		return
				genericMethod.getMethod().getName().indexOf(GET_PREFIX) == 0 &&
						genericMethod.getMethod().getParameterCount() == 0 &&
						actualType.get() != void.class;
	}

	private boolean isBuilder(GenericMethod genericMethod) {
		return
				genericMethod.getMethod().getName().indexOf(BUILDER_PREFIX) == 0 &&
						genericMethod.getMethod().getParameterCount() == 1 &&
						genericMethod.getMethod().getDeclaringClass().equals(genericMethod.getReturnType().getOwnDeclaration().getTypeResolution().getSpecificClass().get());
	}

	private Optional<FieldAccessor> parse(GenericMethod genericMethod, String prefix, FieldAccessorType type, Annotation[] annotations) {
		String methodName = genericMethod.getMethod().getName();
		String fieldName = beanNamingExpert.getFieldName(methodName, prefix);
		ParsedType genericReturnType = genericMethod.getReturnType();
		List<ParsedType> parameters = new ArrayList<>();
		Parameter[] parameterTypes = genericMethod.getMethod().getParameters();
		for (Parameter parameterType : parameterTypes) {
			parameters.add(typeParser.parse(parameterType.getParameterizedType()));
		}
		return of(new FieldAccessor(methodName, fieldName, prefix, type, genericReturnType, parameters, annotations));
	}
}
