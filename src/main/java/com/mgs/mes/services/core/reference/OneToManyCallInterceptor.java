package com.mgs.mes.services.core.reference;

import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.entity.factory.entity.entityData.EntityCallInterceptor;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.OneToMany;
import com.mgs.mes.model.OneToOne;
import com.mgs.mes.v2.entity.method.EntityMethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OneToManyCallInterceptor<T extends Entity> extends EntityCallInterceptor implements InvocationHandler, OneToMany<T> {
	private final EntityData entityData;

	public OneToManyCallInterceptor(EntityData entityData, Map<String, EntityMethodInterceptor> methodInterceptors) {
		super(null, null, entityData, methodInterceptors);
		this.entityData = entityData;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().equals("getList")) {
			return getList();
		} else if (method.getName().equals("retrieveAll")) {
			return retrieveAll();
		} else {
			return entityData.get(method.getName());
		}
	}

	@Override
	public List<OneToOne<T>> getList() {
		//noinspection unchecked
		return (List<OneToOne<T>>) entityData.get("getList");
	}

	@Override
	public List<T> retrieveAll() {
		return getList().stream().map(OneToOne::retrieve).collect(Collectors.toList());
	}
}
