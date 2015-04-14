package com.mgs.mes.build.factory.core;

import com.mgs.mes.build.factory.entity.EntityFactory;
import com.mgs.mes.context.EntityDescriptor;
import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.db.MongoDao;
import com.mgs.mes.meta.utils.Entities;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mongodb.DBObject;

public class EntityRetrieverFactory {
	private final EntityFactory<DBObject> dbObjectEntityFactory;
	private final Entities entities;

	public EntityRetrieverFactory(EntityFactory<DBObject> dbObjectEntityFactory, Entities entities) {
		this.dbObjectEntityFactory = dbObjectEntityFactory;
		this.entities = entities;
	}

	public <T extends Entity, Z extends EntityBuilder<T>> EntityRetriever<T> createRetriever(MongoDao mongoDao, EntityDescriptor<T, Z> entityDescriptor) {
		return new EntityRetriever<>(
				entities,
				mongoDao,
				dbObjectEntityFactory,
				entityDescriptor.getEntityType()
		);
	}
}
