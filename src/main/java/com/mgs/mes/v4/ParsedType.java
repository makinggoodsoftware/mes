package com.mgs.mes.v4;

import java.util.Map;

public class ParsedType {
	private final Declaration ownDeclaration;
	private final Map<Class, ParsedType> superDeclarations;

	public ParsedType(Declaration ownDeclaration, Map<Class, ParsedType> superDeclarations) {
		this.ownDeclaration = ownDeclaration;
		this.superDeclarations = superDeclarations;
	}

	public Declaration getOwnDeclaration() {
		return ownDeclaration;
	}

	public Map<Class, ParsedType> getSuperDeclarations() {
		return superDeclarations;
	}
}
