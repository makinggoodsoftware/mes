package com.mgs.mes.model.factory.entityData;

import com.mgs.mes.model.data.EntityData;
import com.mgs.mes.model.entity.Entity;
import com.mgs.mes.model.factory.EntityFactory;

import static java.lang.reflect.Proxy.newProxyInstance;

public class EntityDataEntityFactory implements EntityFactory<EntityData> {

	public EntityDataEntityFactory() {
	}

	@Override
	public <T extends Entity> T from(Class<T> modelType, EntityData entityData) {
		//noinspection unchecked
		return (T) newProxyInstance(
				EntityDataEntityFactory.class.getClassLoader(),
				new Class[]{modelType},
				new EntityCallInterceptor(entityData)
		);
	}
}
