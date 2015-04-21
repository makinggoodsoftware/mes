package com.mgs.mes.services.core.reference;

import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.entity.factory.entity.entityData.EntityCallInterceptor;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.OneToMany;
import com.mgs.mes.model.OneToOne;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class OneToManyCallInterceptor<T extends Entity> extends EntityCallInterceptor implements InvocationHandler, OneToMany<T> {
	public OneToManyCallInterceptor(EntityData entityData) {
		super(entityData);
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
