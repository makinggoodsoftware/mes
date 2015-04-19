package com.mgs.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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

	public FieldAccessorParser(BeanNamingExpert beanNamingExpert) {
		this.beanNamingExpert = beanNamingExpert;
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
			List<ParametrizedType> parametrizedType = extractGenericClasses(method.getGenericReturnType());
			return parse(method.getReturnType(), method.getName(), GET_PREFIX, GET, parametrizedType);
		}

		if (isBuilder(method)) {
			List<ParametrizedType> parametrizedType  = extractGenericClasses(method.getGenericParameterTypes()[0]);
			return parse(method.getParameters()[0].getType(), method.getName(), BUILDER_PREFIX, BUILDER, parametrizedType);
		}

		return empty();
	}

	private List<ParametrizedType> extractGenericClasses(Type genericReturnType) {
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

	private Optional<FieldAccessor> parse(Class<?> declaredType, String methodName, String prefix, FieldAccessorType type, List<ParametrizedType> parametrizedTypes) {
		String fieldName = beanNamingExpert.getFieldName(methodName, prefix);
		return of(new FieldAccessor(declaredType, methodName, fieldName, prefix, type, parametrizedTypes));
	}
}
