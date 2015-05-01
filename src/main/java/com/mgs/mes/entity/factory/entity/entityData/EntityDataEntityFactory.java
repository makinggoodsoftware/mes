package com.mgs.mes.entity.factory.entity.entityData;

import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.entity.factory.entity.EntityFactory;
import com.mgs.mes.model.Entity;

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
				new EntityCallInterceptor(modelType, null, entityData, null)
		);
	}
}
