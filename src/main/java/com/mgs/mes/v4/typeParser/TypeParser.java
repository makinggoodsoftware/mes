package com.mgs.mes.v4.typeParser;

import com.mgs.mes.v3.reflection.GenericMethod;
import com.mgs.mes.v3.reflection.GenericMethods;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.of;

public class TypeParser {
	public ParsedType parse(Type type) {
		return parse(type, new HashMap<>());
	}

	private ParsedType parse(Type type, Map<String, Declaration> effectiveParameters) {
		TypeResolution typeResolution = typeResolution(type);
		return parse(typeResolution, effectiveParameters);
	}

	private ParsedType parse(TypeResolution typeResolution, Map<String, Declaration> effectiveParameters) {
		Declaration ownDeclaration = null;
		Optional<String> genericName = typeResolution.getGenericName();

		if (genericName.isPresent()) {
			ownDeclaration = effectiveParameters.get(genericName.get());
		}

		if (ownDeclaration == null) {
			ownDeclaration = buildDeclaration(typeResolution, effectiveParameters);
		}

		return parse(ownDeclaration);
	}

	public ParsedType parse(Declaration declaration) {
		Optional<Class> specificClass = declaration.getTypeResolution().getSpecificClass();
		if (!specificClass.isPresent()) return new ParsedType(declaration, new HashMap<>());

		Map<Class, ParsedType> superDeclarations = new HashMap<>();
		accumulateSuperParameterizedTypes(specificClass.get(), declaration.getParameters(), superDeclarations);
		return new ParsedType(declaration, superDeclarations);
	}

	public GenericMethods parseMethods(ParsedType type) {
		Class theClass = type.getOwnDeclaration().getTypeResolution().getSpecificClass().get();
		Method[] methods = theClass.getMethods();
		Map<String, GenericMethod> asMap = new HashMap<>();
		for (Method method : methods) {
			asMap.put(method.getName(), parseMethod (type, method));
		}
		return new GenericMethods(asMap);
	}

	public GenericMethod parseMethod(ParsedType type, Method method) {
		TypeResolution genericTypeResolution = typeResolution(method.getGenericReturnType());
		ParsedType returnType;
		if (method.getDeclaringClass().equals(type.getOwnDeclaration().getActualType().get())){
			returnType = parse(genericTypeResolution, type.getOwnDeclaration().getParameters());
		}else{
			ParsedType superParsedType = type.getSuperDeclarations().get(method.getDeclaringClass());
			if (superParsedType != null){
				Map<String, Declaration> parameters = superParsedType.getOwnDeclaration().getParameters();
				returnType = parse(genericTypeResolution, parameters);
			}else {
				returnType = parse(genericTypeResolution, new HashMap<String, Declaration>());
			}
		}
		return new GenericMethod(returnType, method);
	}

	private void accumulateSuperParameterizedTypes(Class specificClass, Map<String, Declaration> thisEffectiveParameters, Map<Class, ParsedType> accumulator) {
		Type[] superInterfaces = specificClass.getGenericInterfaces();
		for (Type genericInterface : superInterfaces) {
			TypeResolution superTypeResolution = typeResolution(genericInterface);
			if (! superTypeResolution.getParameterizedType().isPresent()) {
				accumulateSuperParameterizedTypes(superTypeResolution.getSpecificClass().get(), new HashMap<>(), accumulator);
				return;
			}
			ParsedType superInterface = parse(superTypeResolution, thisEffectiveParameters);
			accumulator.put(superTypeResolution.getSpecificClass().get(), superInterface);
			accumulateSuperParameterizedTypes(superTypeResolution.getSpecificClass().get(), thisEffectiveParameters, accumulator);
		}
	}

	private Declaration buildDeclaration(TypeResolution typeResolution, Map<String, Declaration> effectiveParameters) {
		if (!typeResolution.getParameterizedType().isPresent()) {
			return new Declaration(typeResolution, new HashMap<>());
		}

		Map<String, Declaration> ownParameters = new HashMap<>();
		ParameterizedType parameterizedType = typeResolution.getParameterizedType().get();
		TypeVariable[] typeParameters = ((Class) parameterizedType.getRawType()).getTypeParameters();
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		int i = 0;
		for (Type actualTypeArgument : actualTypeArguments) {
			String parameterName = typeParameters[i++].getName();
			TypeResolution actualTypeResolution = typeResolution(actualTypeArgument);
			Declaration declaration;
			if (! actualTypeResolution.getSpecificClass().isPresent()){
				declaration = effectiveParameters.get(actualTypeArgument.getTypeName());
			}else{
				declaration = buildDeclaration(actualTypeResolution, effectiveParameters);
			}
			ownParameters.put(
					parameterName,
					declaration
			);
		}
		return new Declaration(typeResolution, ownParameters);
	}

	private TypeResolution typeResolution(Type type) {
		String genericName = null;
		Class specificClass = null;
		ParameterizedType parameterizedType = null;

		boolean parameterized = isParameterized(type);
		if (parameterized) {
			parameterizedType = (ParameterizedType) type;
			specificClass = (Class) parameterizedType.getRawType();
		} else if (Class.class.isAssignableFrom(type.getClass())) {
			specificClass = (Class) type;
		} else {
			genericName = type.getTypeName();
		}

		return new TypeResolution(
				(genericName == null) ? Optional.<String>empty() : of(genericName),
				(specificClass == null) ? Optional.<Class>empty() : of(specificClass),
				(parameterizedType == null) ? Optional.<ParameterizedType>empty() : of(parameterizedType)
		);
	}

	private boolean isParameterized(Type type) {
		return ParameterizedType.class.isAssignableFrom(type.getClass());
	}
}
