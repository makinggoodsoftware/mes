package com.mgs.mes.v3.reflection;

import com.mgs.reflection.ParsedType;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class GenericsExpert {
	private final ParsedTypeFactory parsedTypeFactory;

	public GenericsExpert(ParsedTypeFactory parsedTypeFactory) {
		this.parsedTypeFactory = parsedTypeFactory;
	}

	public ParsedType parseMethodReturnType(Method toParse) {
		return parseType(toParse.getGenericReturnType());

	}

	private ParsedType parseType(Type type) {
		ParameterizedType thisParameterizedType = toParametrizedType(type);
		if (thisParameterizedType == null) {
			if (Class.class.isAssignableFrom(type.getClass())){
				return parsedTypeFactory.specificLeaf((Class) type, type);
			}
			return parsedTypeFactory.unresolvedLeaf(type);
		}

		String thisClassName = getThisClassName(thisParameterizedType.getTypeName());
		Optional<Class> thisClass = loadClass (thisClassName);
		Type[] actualTypeArguments = thisParameterizedType.getActualTypeArguments();
		if (actualTypeArguments.length == 0) throw new IllegalStateException();

		List<ParsedType> genericTypes = Stream.of(actualTypeArguments).
				map(this::parseType).
				collect(toList());

		return parsedTypeFactory.parametrizedContainer(thisClass.get(), type, genericTypes);
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
}
