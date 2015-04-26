package com.mgs.mes.v2.entity;

import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.entity.factory.entity.entityData.EntityCallInterceptor;
import com.mgs.mes.model.Entity;
import com.mongodb.DBObject;

import java.util.Map;

import static java.lang.reflect.Proxy.newProxyInstance;

public class EntityProxyFactory {
	public <T extends Entity> T from (Class<T> entityType, DBObject dbObject, Map<String, Object> domainValues){
		//noinspection unchecked
		return (T) newProxyInstance(
				EntityFactory.class.getClassLoader(),
				new Class[]{entityType},
				new EntityCallInterceptor(new EntityData(
						dbObject,
						domainValues
				))
		);
	}
}
