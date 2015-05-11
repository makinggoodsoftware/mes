package com.mgs.reflection;

import java.util.Map;
import java.util.Optional;

public class ParsedType {
	private final Optional<Class> actualType;
	private final String typeName;
	private final boolean isResolved;
	private final boolean isGenerics;
	private final Map<String, ParsedType> parametirizedTypes;

	public ParsedType(Optional<Class> actualType, String typeName, boolean isResolved, boolean isGenerics, Map<String, ParsedType> parametirizedTypes) {
		this.actualType = actualType;
		this.typeName = typeName;
		this.isResolved = isResolved;
		this.isGenerics = isGenerics;
		this.parametirizedTypes = parametirizedTypes;
	}

	public Map<String, ParsedType> getParameters() {
		return parametirizedTypes;
	}

	public boolean isParametrized() {
		return isGenerics;
	}

	public boolean isResolved() {
		return isResolved;
	}

	public String getTypeName() {
		return typeName;
	}

	public Optional<Class> getActualType() {
		return actualType;
	}
}
