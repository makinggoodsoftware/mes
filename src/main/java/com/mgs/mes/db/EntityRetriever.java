package com.mgs.mes.db;

import com.mgs.mes.build.factory.entity.EntityFactory;
import com.mgs.mes.meta.Entities;
import com.mgs.mes.model.Entity;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import java.util.Iterator;
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
		DBCursor dbObjects = mongoDao.find(entities.collectionName(type), new BasicDBObject("_id", id));
		Iterator<DBObject> iterator = dbObjects.iterator();
		if (! iterator.hasNext()) return empty();

		DBObject fromDb = iterator.next();
		if (iterator.hasNext()) throw new IllegalStateException("More than one element with the same ID");
		return of(entityFactory.from(type, fromDb));
	}
}
