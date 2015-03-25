package com.mgs.config.mes.build;

import com.mgs.mes.build.data.EntityData;
import com.mgs.mes.build.factory.entity.EntityFactory;
import com.mgs.mes.build.factory.entity.entityData.EntityDataEntityFactory;

public class CommonConfig {
	public EntityFactory<EntityData> entityDataEntityFactory() {
		return new EntityDataEntityFactory();
	}
}
