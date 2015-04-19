package com.mgs.reflection;

import java.util.List;

public class FieldAccessor {
	private final FieldAccessorType type;
	private final String methodName;
	private final String prefix;
	private final String fieldName;
	private final Class<?> declaredType;
	private final List<ParametrizedType> parametrizedTypes;

	public FieldAccessor(Class<?> declaredType, String methodName, String fieldName, String prefix, FieldAccessorType type, List<ParametrizedType> parametrizedTypes) {
		this.declaredType = declaredType;
		this.methodName = methodName;
		this.type = type;
		this.prefix = prefix;
		this.fieldName = fieldName;
		this.parametrizedTypes = parametrizedTypes;
	}

	public FieldAccessorType getType() {
		return type;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Class<?> getDeclaredType() {
		return declaredType;
	}

	public String getMethodName() {
		return methodName;
	}

	public List<ParametrizedType> getParametrizedTypes() {
		return parametrizedTypes;
	}

	@SuppressWarnings("RedundantIfStatement")
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FieldAccessor)) return false;

		FieldAccessor that = (FieldAccessor) o;

		if (!declaredType.equals(that.declaredType)) return false;
		if (!fieldName.equals(that.fieldName)) return false;
		if (!methodName.equals(that.methodName)) return false;
		if (!prefix.equals(that.prefix)) return false;
		if (type != that.type) return false;
		if (!parametrizedTypes.equals(that.parametrizedTypes)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = type.hashCode();
		result = 31 * result + methodName.hashCode();
		result = 31 * result + prefix.hashCode();
		result = 31 * result + fieldName.hashCode();
		result = 31 * result + declaredType.hashCode();
		result = 31 * result + parametrizedTypes.hashCode();
		return result;
	}

	@SuppressWarnings("StringBufferReplaceableByString")
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("FieldAccessor{");
		sb.append("type=").append(type);
		sb.append(", methodName='").append(methodName).append('\'');
		sb.append(", prefix='").append(prefix).append('\'');
		sb.append(", fieldName='").append(fieldName).append('\'');
		sb.append(", declaredType=").append(declaredType);
		sb.append(", parametrizedTypes=").append(parametrizedTypes);
		sb.append('}');
		return sb.toString();
	}
}
