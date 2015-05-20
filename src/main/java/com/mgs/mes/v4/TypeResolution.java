package com.mgs.mes.v4;


import java.lang.reflect.ParameterizedType;
import java.util.Optional;

public class TypeResolution {
	private final String typeName;
	private final boolean isParameterized;
	private final Optional<ParameterizedType> parameterizedType;
	private final Optional<Class> specificClass;

	public TypeResolution(String typeName, Optional<Class> specificClass, boolean isParameterized, Optional<ParameterizedType> parameterizedType) {
		this.typeName = typeName;
		this.specificClass = specificClass;
		this.isParameterized = isParameterized;
		this.parameterizedType = parameterizedType;
	}

	public String getTypeName() {
		return typeName;
	}

	public Optional<Class> getSpecificClass() {
		return specificClass;
	}

	public boolean isParameterized() {
		return isParameterized;
	}

	public Optional<ParameterizedType> getParameterizedType() {
		return parameterizedType;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("TypeResolution{");
		sb.append("isParameterized=").append(isParameterized);
		sb.append(", typeName='").append(typeName).append('\'');
		sb.append(", parameterizedType=").append(parameterizedType);
		sb.append(", specificClass=").append(specificClass);
		sb.append('}');
		return sb.toString();
	}
}
