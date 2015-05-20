package com.mgs.reflection;

import java.util.Map;
import java.util.Optional;

public class GenericType {
	private final Optional<Class> actualType;
	private final String typeName;
	private final boolean isResolved;
	private final boolean isGenerics;
	private final Map<Class, Map<String, GenericType>> parametirizedTypes;

	public GenericType(Optional<Class> actualType, String typeName, boolean isResolved, boolean isGenerics, Map<Class, Map<String, GenericType>> parametirizedTypes) {
		this.actualType = actualType;
		this.typeName = typeName;
		this.isResolved = isResolved;
		this.isGenerics = isGenerics;
		this.parametirizedTypes = parametirizedTypes;
	}

	public Map<Class, Map<String, GenericType>> getParameters() {
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
