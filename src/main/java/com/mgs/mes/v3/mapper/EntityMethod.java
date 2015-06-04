package com.mgs.mes.v3.mapper;

@FunctionalInterface
public interface EntityMethod <T extends MapEntity>{
	Object execute (T value, EntityMapCallInterceptor<T> interceptor, Object[] params);
}
