package com.mgs.mes.db;

import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.model.factory.ModelFactory;
import com.mgs.mes.utils.MongoEntities;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import java.util.Iterator;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class MongoRetriever <T extends MongoEntity> {
	private final MongoEntities mongoEntities;
	private final MongoDao mongoDao;
	private final ModelFactory<DBObject> modelFactory;
	private final Class<T> type;

	public MongoRetriever(MongoEntities mongoEntities, MongoDao mongoDao, ModelFactory<DBObject> modelFactory, Class<T> type) {
		this.mongoEntities = mongoEntities;
		this.mongoDao = mongoDao;
		this.modelFactory = modelFactory;
		this.type = type;
	}

	public Optional<T> byId(ObjectId id) {
		DBCursor dbObjects = mongoDao.find(mongoEntities.collectionName(type), new BasicDBObject("_id", id));
		Iterator<DBObject> iterator = dbObjects.iterator();
		if (! iterator.hasNext()) return empty();

		DBObject fromDb = iterator.next();
		if (iterator.hasNext()) throw new IllegalStateException("More than one element with the same ID");
		return of(modelFactory.from(type, fromDb));
	}
}
