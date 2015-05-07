package com.mgs.reflection;

import java.util.List;
import java.util.Optional;

public class ParametrizedType {
	private final String name;
	private final Optional<Class> specificClass;
	private final Optional<List<ParametrizedType>> childrenParametrizedTypes;

	public ParametrizedType(String name, Optional<Class> specificClass, Optional<List<ParametrizedType>> childrenParametrizedTypes) {
		this.name = name;
		this.specificClass = specificClass;
		this.childrenParametrizedTypes = childrenParametrizedTypes;
	}

	@SuppressWarnings("UnusedDeclaration")
	public String getName() {
		return name;
	}

	public Optional<Class> getSpecificClass() {
		return specificClass;
	}

	public Optional<List<ParametrizedType>> getChildrenParametrizedTypes() {
		return childrenParametrizedTypes;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ParametrizedType)) return false;

		ParametrizedType that = (ParametrizedType) o;

		if (!name.equals(that.name)) return false;
		//noinspection RedundantIfStatement
		if (!specificClass.equals(that.specificClass)) return false;
		if (!childrenParametrizedTypes.equals(that.childrenParametrizedTypes)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + specificClass.hashCode();
		result = 31 * result + childrenParametrizedTypes.hashCode();
		return result;
	}

	@Override
	public String toString() {
		//noinspection StringBufferReplaceableByString,StringBufferMayBeStringBuilder
		final StringBuffer sb = new StringBuffer("ParametrizedType{");
		sb.append("name='").append(name).append('\'');
		sb.append(", specificClass=").append(specificClass);
		sb.append(", childrenParametrizedTypes=").append(childrenParametrizedTypes);
		sb.append('}');
		return sb.toString();
	}
}
