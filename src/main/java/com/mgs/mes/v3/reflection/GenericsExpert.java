package com.mgs.mes.v3.reflection;

import com.mgs.reflection.GenericType;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class GenericsExpert {
	private final ParsedTypeFactory parsedTypeFactory;

	public GenericsExpert(ParsedTypeFactory parsedTypeFactory) {
		this.parsedTypeFactory = parsedTypeFactory;
	}

	public GenericType parseMethodReturnType(Method toParse) {
		return parseType(toParse.getGenericReturnType());

	}

	public GenericType parseType(Type toParse) {
		return parseType(toParse, new HashMap<>());
	}

	public GenericType parseType(Type toParse, Map<String, GenericType> parameters) {
		ParameterizedType thisParameterizedType = toParametrizedType(toParse);
		if (thisParameterizedType == null) {
			if (Class.class.isAssignableFrom(toParse.getClass())){
				return parsedTypeFactory.specificLeaf((Class) toParse, toParse);
			}
			GenericType parametrizedType = parameters.get(toParse.getTypeName());
			if (parametrizedType != null) return parametrizedType;
			return parsedTypeFactory.unresolvedLeaf(toParse);
		}

		String thisClassName = getThisClassName(thisParameterizedType.getTypeName());
		Optional<Class> thisClass = loadClass (thisClassName);
		Type[] actualTypeArguments = thisParameterizedType.getActualTypeArguments();
		if (actualTypeArguments.length == 0) throw new IllegalStateException();

		List<GenericType> genericTypes = Stream.of(actualTypeArguments).
				map((toParse1) -> parseType(toParse1)).
				collect(toList());

		return parsedTypeFactory.parametrizedContainer(thisClass.get(), toParse, genericTypes);
	}

	private Optional<Class> loadClass(String thisClassName) {
		try {
			return Optional.of(Class.forName(thisClassName));
		} catch (ClassNotFoundException e) {
			return Optional.empty();
		}
	}

	private String getThisClassName(String parametrizedClassName) {
		int firstParamPos = parametrizedClassName.indexOf("<");
		if (firstParamPos < 0) return parametrizedClassName;

		return parametrizedClassName.substring(0, firstParamPos);
	}

	private ParameterizedType toParametrizedType(Type type) {
		if (type == null) return null;
		if (!(type instanceof ParameterizedType)) return null;
		return (ParameterizedType) type;
	}

	public GenericMethods parseMethods(GenericType type) {
		if (! type.isResolved()) throw new IllegalArgumentException("Can't resolve type");

		Map<String, GenericMethod> parsedMethodsAsMap = new HashMap<>();
		Method[] methods = type.getActualType().get().getMethods();
		for (Method method : methods) {
			parsedMethodsAsMap.put(method.getName(), new GenericMethod(parseType(method.getGenericReturnType(), type.getParameters()), method));
		}
		GenericMethods result = new GenericMethods(parsedMethodsAsMap);
		return result;
	}

	public GenericMethod parseMethod(Method method) {
		return new GenericMethod(parseMethodReturnType(method), method);
	}
}
