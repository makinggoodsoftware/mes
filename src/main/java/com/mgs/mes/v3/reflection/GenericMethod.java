package com.mgs.mes.v3.reflection;

import com.mgs.reflection.GenericType;

import java.lang.reflect.Method;

public class GenericMethod {
	private final GenericType returnType;
	private final Method method;

	public GenericMethod(GenericType returnType, Method method) {
		this.returnType = returnType;
		this.method = method;
	}

	public GenericType getReturnType() {
		return returnType;
	}

	public Method getMethod() {
		return method;
	}
}
