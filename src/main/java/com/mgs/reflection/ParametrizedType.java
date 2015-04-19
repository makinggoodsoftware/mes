package com.mgs.reflection;

import java.util.Optional;

public class ParametrizedType {
	private final String name;
	private final Optional<Class> specificClass;

	public ParametrizedType(String name, Optional<Class> specificClass) {
		this.name = name;
		this.specificClass = specificClass;
	}

	@SuppressWarnings("UnusedDeclaration")
	public String getName() {
		return name;
	}

	public Optional<Class> getSpecificClass() {
		return specificClass;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ParametrizedType)) return false;

		ParametrizedType that = (ParametrizedType) o;

		if (!name.equals(that.name)) return false;
		//noinspection RedundantIfStatement
		if (!specificClass.equals(that.specificClass)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + specificClass.hashCode();
		return result;
	}

	@Override
	public String toString() {
		//noinspection StringBufferReplaceableByString,StringBufferMayBeStringBuilder
		final StringBuffer sb = new StringBuffer("ParametrizedType{");
		sb.append("name='").append(name).append('\'');
		sb.append(", specificClass=").append(specificClass);
		sb.append('}');
		return sb.toString();
	}
}
