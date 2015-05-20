package com.mgs.mes.v4;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TypeParser {
	public ParsedType parse(Type type) {
		TypeResolution typeResolution = typeResolution(type);
		Declaration ownDeclaration = buildDeclaration(typeResolution);

		Optional<Class> specificClass = typeResolution.getSpecificClass();
		if (!specificClass.isPresent()) return new ParsedType(ownDeclaration, new HashMap<>());

		HashMap<Class, ParsedType> superDeclarations = new HashMap<>();
		accumulateSuperParameterizedTypes(specificClass.get(), superDeclarations);
		return new ParsedType(ownDeclaration, superDeclarations);
	}

	private void accumulateSuperParameterizedTypes(Class specificClass, HashMap<Class, ParsedType> superDeclarations) {
		Type[] genericInterfaces = specificClass.getGenericInterfaces();
		for (Type genericInterface : genericInterfaces) {
			ParsedType superInterface = parse(genericInterface);
			TypeResolution superTypeResolution = superInterface.getOwnDeclaration().getTypeResolution();
			accumulateSuperParameterizedTypes(superTypeResolution.getSpecificClass().get(), superDeclarations);
			if (superTypeResolution.isParameterized()){
				superDeclarations.put(superTypeResolution.getSpecificClass().get(), superInterface);
			}
		}
	}

	private Declaration buildDeclaration(TypeResolution typeResolution) {
		Map<String, Declaration> ownParameters = new HashMap<>();
		if (typeResolution.isParameterized()) {
			ParameterizedType parameterizedType = typeResolution.getParameterizedType().get();
			TypeVariable[] typeParameters = ((Class) parameterizedType.getRawType()).getTypeParameters();
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			int i = 0;
			for (Type actualTypeArgument : actualTypeArguments) {
				ownParameters.put(
						typeParameters[i++].getName(),
						buildDeclaration(typeResolution(actualTypeArgument))
				);
			}
		}
		return new Declaration(typeResolution, ownParameters);
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
				(specificClass == null) ? Optional.<Class>empty() : Optional.of(specificClass),
				parameterized,
				(parameterizedType == null) ? Optional.<ParameterizedType>empty() : Optional.of(parameterizedType)
		);
	}

	private boolean isParameterized(Type type) {
		return ParameterizedType.class.isAssignableFrom(type.getClass());
	}
}
