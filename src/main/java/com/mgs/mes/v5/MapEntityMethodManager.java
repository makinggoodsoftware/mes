package com.mgs.mes.v5;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class MapEntityMethodManager {
	private final Object manager;
	private final Method delegator;

	public MapEntityMethodManager(Object manager, Method delegator) {
		this.manager = manager;
		this.delegator = delegator;
	}

	public Object delegate(Class type, Object proxy, Method method, Map<String, Object> domainMap, Map<String, Object> valueMap, boolean open, Object[] args) {
		try {
			return delegator.invoke(manager, proxy, domainMap, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}
}
