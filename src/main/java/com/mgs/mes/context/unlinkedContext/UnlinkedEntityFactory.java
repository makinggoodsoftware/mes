package com.mgs.mes.context.unlinkedContext;

import com.mgs.mes.build.factory.entity.EntityFactory;
import com.mgs.mes.context.EntityDescriptor;
import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.db.MongoDao;
import com.mgs.mes.meta.utils.Entities;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mongodb.DBObject;

public class UnlinkedEntityFactory {
	private final MongoDao mongoDao;
	private final EntityFactory<DBObject> dbObjectEntityFactory;
	private final Entities entities;


	public UnlinkedEntityFactory(MongoDao mongoDao, EntityFactory<DBObject> dbObjectEntityFactory, Entities entities) {
		this.mongoDao = mongoDao;
		this.dbObjectEntityFactory = dbObjectEntityFactory;
		this.entities = entities;
	}

	private <T extends Entity> EntityRetriever<T> retriever(Class<T> retrieveType) {
		return new EntityRetriever<>(entities, mongoDao, dbObjectEntityFactory, retrieveType);
	}



	public <T extends Entity, Z extends EntityBuilder<T>>
	UnlinkedEntity<T, Z> create(EntityDescriptor<T, Z> entityDescriptor) {
		EntityRetriever<T> retriever = retriever(entityDescriptor.getEntityType());
		return new UnlinkedEntity<>(retriever, entityDescriptor);
	}
}
