package com.mgs.mes.v4;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

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
		Declaration ownDeclaration = buildDeclaration(typeResolution, effectiveParameters);

		Optional<Class> specificClass = typeResolution.getSpecificClass();
		if (!specificClass.isPresent()) return new ParsedType(ownDeclaration, new HashMap<>());

		Map<Class, ParsedType> superDeclarations = new HashMap<>();
		accumulateSuperParameterizedTypes(specificClass.get(), ownDeclaration.getParameters(), superDeclarations);
		return new ParsedType(ownDeclaration, superDeclarations);
	}

	private void accumulateSuperParameterizedTypes(Class specificClass, Map<String, Declaration> effectiveParameters, Map<Class, ParsedType> accumulator) {
		Type[] genericInterfaces = specificClass.getGenericInterfaces();
		for (Type genericInterface : genericInterfaces) {
			ParsedType superInterface = parse(genericInterface, substituteParameters((ParameterizedTypeImpl) genericInterface, effectiveParameters));
			TypeResolution superTypeResolution = superInterface.getOwnDeclaration().getTypeResolution();
			accumulateSuperParameterizedTypes(superTypeResolution.getSpecificClass().get(), effectiveParameters, accumulator);
			if (superTypeResolution.isParameterized()){
				accumulator.put(superTypeResolution.getSpecificClass().get(), superInterface);
			}
		}
	}

	private Map<String, Declaration> substituteParameters(ParameterizedTypeImpl parameterizedType, Map<String, Declaration> effectiveParameters) {
		Map<String, Declaration> substitutedParameters = new HashMap<>();
		Type[] sourceActualTypes = parameterizedType.getActualTypeArguments();
		Type[] targetActualTypes = parameterizedType.getRawType().getTypeParameters();
		for (int i=0; i<sourceActualTypes.length; i++){
			Type sourceActualType = sourceActualTypes[i];
			Type targetActualType = targetActualTypes[i];
			substitutedParameters.put(targetActualType.getTypeName(), effectiveParameters.get(sourceActualType.getTypeName()));
		}
		return substitutedParameters;
	}

	private Declaration buildDeclaration(TypeResolution typeResolution, Map<String, Declaration> effectiveParameters) {
		if (!typeResolution.isParameterized()) {
			return new Declaration(typeResolution, new HashMap<>());
		}

		Map<String, Declaration> ownParameters = new HashMap<>();
		ParameterizedType parameterizedType = typeResolution.getParameterizedType().get();
		TypeVariable[] typeParameters = ((Class) parameterizedType.getRawType()).getTypeParameters();
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		int i = 0;
		for (Type actualTypeArgument : actualTypeArguments) {
			String parameterName = typeParameters[i++].getName();
			Declaration declaration = resolveDeclaration(parameterName, effectiveParameters, actualTypeArgument);
			ownParameters.put(
					parameterName,
					declaration
			);
		}
		return new Declaration(typeResolution, ownParameters);
	}

	private Declaration resolveDeclaration(String parameterName, Map<String, Declaration> effectiveParameters, Type actualTypeArgument) {
		TypeResolution resolvedParameterTypeResolution;
		Declaration passedParameter = effectiveParameters.get(parameterName);
		if (passedParameter != null) {
			resolvedParameterTypeResolution = passedParameter.getTypeResolution();
		}else{
			resolvedParameterTypeResolution = typeResolution(actualTypeArgument);
		}
		return buildDeclaration(resolvedParameterTypeResolution, effectiveParameters);
	}

	private TypeResolution typeResolution(Type type) {
		Class specificClass = null;
		ParameterizedType parameterizedType = null;

		boolean parameterized = isParameterized(type);
		if (parameterized) {
			parameterizedType = (ParameterizedType) type;
			specificClass = (Class) parameterizedType.getRawType();
		} else if (Class.class.isAssignableFrom(type.getClass())) {
			specificClass = (Class) type;
		}

		return new TypeResolution(
				type.getTypeName(),
				(specificClass == null) ? Optional.<Class>empty() : of(specificClass),
				parameterized,
				(parameterizedType == null) ? Optional.<ParameterizedType>empty() : of(parameterizedType)
		);
	}

	private boolean isParameterized(Type type) {
		return ParameterizedType.class.isAssignableFrom(type.getClass());
	}
}
