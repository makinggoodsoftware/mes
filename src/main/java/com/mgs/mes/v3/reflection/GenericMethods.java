package com.mgs.mes.v3.reflection;

import java.util.Map;

public class GenericMethods {
	private final Map<String, GenericMethod> parsedMethodsAsMap;

	public GenericMethods(Map<String, GenericMethod> parsedMethodsAsMap) {
		this.parsedMethodsAsMap = parsedMethodsAsMap;
	}

	public Map<String, GenericMethod> getParsedMethodsAsMap() {
		return parsedMethodsAsMap;
	}
}
