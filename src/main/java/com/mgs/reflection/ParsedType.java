package com.mgs.reflection;

import java.util.List;
import java.util.Optional;

public class ParsedType {
	private final Optional<Class> actualType;
	private final String typeName;
	private final boolean isResolved;
	private final boolean isGenerics;
	private final List<ParsedType> childTypes;

	public ParsedType(Optional<Class> actualType, String typeName, boolean isResolved, boolean isGenerics, List<ParsedType> childTypes) {
		this.actualType = actualType;
		this.typeName = typeName;
		this.isResolved = isResolved;
		this.isGenerics = isGenerics;
		this.childTypes = childTypes;
	}

	public List<ParsedType> getParameters() {
		return childTypes;
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
