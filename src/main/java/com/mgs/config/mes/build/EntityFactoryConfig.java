package com.mgs.config.mes.build;

import com.mgs.mes.build.data.EntityData;
import com.mgs.mes.build.factory.entity.EntityFactory;
import com.mgs.mes.build.factory.entity.entityData.EntityDataEntityFactory;

public class EntityFactoryConfig {
	public EntityFactory<EntityData> entityDataEntityFactory() {
		return new EntityDataEntityFactory();
	}
}
