package com.mgs.config.mes.entity;

import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.entity.factory.entity.EntityFactory;
import com.mgs.mes.entity.factory.entity.entityData.EntityDataEntityFactory;

public class CommonEntityConfig {
	public EntityFactory<EntityData> entityDataEntityFactory() {
		return new EntityDataEntityFactory();
	}
}
