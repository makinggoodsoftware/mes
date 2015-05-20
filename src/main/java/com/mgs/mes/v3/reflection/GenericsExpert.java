package com.mgs.mes.v3.reflection;

import com.mgs.reflection.GenericType;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;

public class GenericsExpert {
	private final ParsedTypeFactory genericTypeFactory;

	public GenericsExpert(ParsedTypeFactory genericTypeFactory) {
		this.genericTypeFactory = genericTypeFactory;
	}

	public GenericType parseMethodReturnType(Class from, String methodName, Class... params) {
		try {
			GenericType parentGenericType = parseType(from);
			Method method = from.getMethod(methodName, params);
			return parseType(method.getGenericReturnType(), parentGenericType.getParameters());
		} catch (NoSuchMethodException e) {
			throw new InvalidParameterException();
		}

	}

	public GenericType parseType(Type toParse) {
		return parseType(toParse, new HashMap<>());
	}

	public GenericType parseType(Type toParse, Map<Class, Map<String, GenericType>> parameters) {
		Map<Class, Map<String, GenericType>> superParameters = escalateUp(toParse);


		if (! isParameterizedType(toParse)) {
			return processNonParameterizedType(toParse, parameters);
		}

		ParameterizedType parameterizedType = ((ParameterizedType) toParse);
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		if (actualTypeArguments.length == 0) throw new IllegalStateException();

		List<GenericType> genericTypes = of(actualTypeArguments).
				map(this::parseType).
				collect(toList());

		return genericTypeFactory.parametrizedContainer((Class) parameterizedType.getRawType(), toParse, genericTypes);
	}

	private Map<Class, Map<String, GenericType>> escalateUp(Type toParse) {
		Map<Class, Map<String, GenericType>> allSuperTypeParameters = new HashMap<>();
		if (isSimpleType(toParse)){
			Class simpleType = (Class) toParse;
			Stream.concat(
					stream(simpleType.getGenericInterfaces()),
					of(simpleType.getGenericSuperclass())
			).
			forEach((superType) -> {
				if (superType != null){
					GenericType superGenericType = parseType(superType, new HashMap<>());
					if (superGenericType.isResolved() && superGenericType.isParametrized()){
						Map<Class, Map<String, GenericType>> superParameters = superGenericType.getParameters();
						allSuperTypeParameters.putAll(superParameters);
					}
				}
			});
		}
		return allSuperTypeParameters;
	}

	private GenericType processNonParameterizedType(Type toParse, Map<Class, Map<String, GenericType>> parameters) {
		if (isSimpleType(toParse)){
			return genericTypeFactory.simpleType(toParse);
		}

		Map<String, GenericType> genericsForThisType = parameters.get(toParse);
		if (genericsForThisType != null){
			GenericType parametrizedType = genericsForThisType.get(toParse.getTypeName());
			if (parametrizedType != null) return parametrizedType;
		}

		return genericTypeFactory.unresolvedLeaf(toParse);
	}

	private boolean isParameterizedType(Type toParse) {
		return ParameterizedType.class.isAssignableFrom(toParse.getClass());
	}

	private boolean isSimpleType(Type toParse) {
		return Class.class.isAssignableFrom(toParse.getClass());
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

	public GenericMethod parseMethod(Class from, String methodName, Class... params) {
		try {
			Method method = from.getMethod(methodName, params);
			return new GenericMethod(parseMethodReturnType(from, methodName, params), method);
		} catch (NoSuchMethodException e) {
			throw new InvalidParameterException();
		}
	}
}
