package com.mgs.mes.entity.factory.entity.dbo;

import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.entity.data.EntityDataFactory;
import com.mgs.mes.entity.factory.entity.EntityFactory;
import com.mgs.mes.model.Entity;
import com.mongodb.DBObject;

public class DBObjectEntityFactory implements EntityFactory<DBObject> {
	private final EntityFactory<EntityData> modelDataEntityFactory;
	private final EntityDataFactory entityDataFactory;

	public DBObjectEntityFactory(EntityFactory<EntityData> modelDataEntityFactory, EntityDataFactory entityDataFactory) {
		this.modelDataEntityFactory = modelDataEntityFactory;
		this.entityDataFactory = entityDataFactory;
	}

	@Override
	public <T extends Entity>T from(Class<T> type, DBObject dbObject){
		EntityData entityData = entityDataFactory.fromDbo(type, dbObject);
		return modelDataEntityFactory.from(type, entityData);
	}

}
