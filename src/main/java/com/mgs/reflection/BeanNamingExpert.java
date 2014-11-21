package com.mgs.reflection;

public class BeanNamingExpert {
	public String getFieldName(String methodName, String prefix) {
		if (methodName.lastIndexOf(prefix)!=0) throw new IllegalArgumentException();

		String fieldNameFirstLetter = methodName.substring(prefix.length(), prefix.length() + 1);
		String fieldNameTail = methodName.substring(prefix.length() + 1, methodName.length());
		return fieldNameFirstLetter.toLowerCase() + fieldNameTail;
	}

	public String getGetterName(String fieldName) {
		String fieldNameTail = fieldName.substring(1, fieldName.length());
		String fieldNameFirstLetter = fieldName.substring(0, 1);
		return "get" + fieldNameFirstLetter.toUpperCase() + fieldNameTail;
	}
}
