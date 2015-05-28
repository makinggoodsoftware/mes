package com.mgs.mes.v4.typeParser;


import java.lang.reflect.ParameterizedType;
import java.util.Optional;

public class TypeResolution {
	private final Optional<String> genericName;
	private final Optional<ParameterizedType> parameterizedType;
	private final Optional<Class> specificClass;

	public TypeResolution(Optional<String> genericName, Optional<Class> specificClass, Optional<ParameterizedType> parameterizedType) {
		this.genericName = genericName;
		this.specificClass = specificClass;
		this.parameterizedType = parameterizedType;
	}

	public Optional<Class> getSpecificClass() {
		return specificClass;
	}

	public Optional<ParameterizedType> getParameterizedType() {
		return parameterizedType;
	}

	public Optional<String> getGenericName() {
		return genericName;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("TypeResolution{");
		sb.append(", genericName=").append(genericName);
		sb.append(", parameterizedType=").append(parameterizedType);
		sb.append(", specificClass=").append(specificClass);
		sb.append('}');
		return sb.toString();
	}
}
