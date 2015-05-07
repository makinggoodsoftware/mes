package com.mgs.mes.v3.mapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

public class EntityMapCallInterceptor<T extends MapEntity> implements InvocationHandler {
	private final Class<T> type;
	private final Map<String, Object> domainMap;
	private final MapEntityManager<T> entityManager;

	public EntityMapCallInterceptor(Class<T> type, Map<String, Object> domainMap, MapEntityManager<T> entityManager) {
		this.type = type;

		this.domainMap = domainMap;
		this.entityManager = entityManager;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Optional<EntityMethod<T>> entityMethod = entityManager.applies(method);
		if (entityMethod.isPresent()){
			//noinspection unchecked
			return entityMethod.get().execute(type, (T) proxy, domainMap, args);
		}
		return domainMap.get(method.getName());
	}

}
