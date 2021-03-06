package com.mgs.mes.services.core;

import com.mgs.mes.db.MongoDao;
import com.mgs.mes.entity.factory.entity.EntityFactory;
import com.mgs.mes.meta.utils.Entities;
import com.mgs.mes.model.Entity;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class EntityRetriever<T extends Entity> {
	private final Entities entities;
	private final MongoDao mongoDao;
	private final EntityFactory<DBObject> entityFactory;
	private final Class<T> type;

	public EntityRetriever(Entities entities, MongoDao mongoDao, EntityFactory<DBObject> entityFactory, Class<T> type) {
		this.entities = entities;
		this.mongoDao = mongoDao;
		this.entityFactory = entityFactory;
		this.type = type;
	}

	public Optional<T> byId(ObjectId id) {
		Optional<DBObject> fromDb = mongoDao.findOne(entities.collectionName(type), new BasicDBObject("_id", id));
		return !fromDb.isPresent() ?
				empty() :
				of(entityFactory.from(type, fromDb.get()));
	}
}
