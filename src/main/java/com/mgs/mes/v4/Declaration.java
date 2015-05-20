package com.mgs.mes.v4;

import java.util.Map;

public class Declaration {
	private final TypeResolution typeResolution;
	private final Map<String, Declaration> parameters;

	public Declaration(TypeResolution typeResolution, Map<String, Declaration> parameters) {
		this.typeResolution = typeResolution;
		this.parameters = parameters;
	}

	public Map<String, Declaration> getParameters() {
		return parameters;
	}

	public TypeResolution getTypeResolution() {
		return typeResolution;
	}
}
