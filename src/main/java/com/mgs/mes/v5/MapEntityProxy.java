package com.mgs.mes.v5;

import com.mgs.mes.v3.mapper.MapEntity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class MapEntityProxy implements InvocationHandler {
	private final Class<? extends MapEntity> type;
	private final Map<String, Object> domainMap;
	private final Map<String, Object> valueMap;
	private final Map<Method, MapEntityMethodManager> methodManagers;
	private final boolean mutable;

	public MapEntityProxy(Class type, Map<String, Object> domainMap, Map<String, Object> valueMap, Map<Method, MapEntityMethodManager> methodManagers, boolean mutable) {
		this.type = type;
		this.domainMap = domainMap;
		this.valueMap = valueMap;
		this.methodManagers = methodManagers;
		this.mutable = mutable;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return methodManagers.get(method).delegate(type, proxy, method, domainMap, valueMap, mutable, args);
	}

	public Map<String, Object> getDomainMap() {
		return domainMap;
	}

	public Map<Method, MapEntityMethodManager> getMethodManagers() {
		return methodManagers;
	}

	public boolean isMutable() {
		return mutable;
	}

	public Class<? extends MapEntity> getType() {
		return type;
	}

	public Map<String, Object> getValueMap() {
		return valueMap;
	}
}
