package com.mgs.reflection;

import com.mgs.mes.v4.typeParser.ParsedType;

import java.lang.annotation.Annotation;
import java.util.Arrays;

public class FieldAccessor {
	private final FieldAccessorType type;
	private final String methodName;
	private final String prefix;
	private final String fieldName;
	private final ParsedType returnType;
	private final Annotation[] annotations;

	public FieldAccessor(String methodName, String fieldName, String prefix, FieldAccessorType type, ParsedType returnType, Annotation[] annotations) {
		this.methodName = methodName;
		this.type = type;
		this.prefix = prefix;
		this.fieldName = fieldName;
		this.returnType = returnType;
		this.annotations = annotations;
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

	public String getMethodName() {
		return methodName;
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}

	public ParsedType getReturnType() {
		return returnType;
	}

	@SuppressWarnings("RedundantIfStatement")
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FieldAccessor)) return false;

		FieldAccessor that = (FieldAccessor) o;

		if (!fieldName.equals(that.fieldName)) return false;
		if (!methodName.equals(that.methodName)) return false;
		if (!prefix.equals(that.prefix)) return false;
		if (type != that.type) return false;
		if (!returnType.equals(that.returnType)) return false;
		if (!Arrays.equals(annotations, that.annotations)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = type.hashCode();
		result = 31 * result + methodName.hashCode();
		result = 31 * result + prefix.hashCode();
		result = 31 * result + fieldName.hashCode();
		result = 31 * result + returnType.hashCode();
		result = 31 * result + Arrays.hashCode(annotations);
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
		sb.append(", returnType=").append(returnType);
		sb.append(", parametrizedTypes=").append(Arrays.toString(annotations));
		sb.append('}');
		return sb.toString();
	}

}
