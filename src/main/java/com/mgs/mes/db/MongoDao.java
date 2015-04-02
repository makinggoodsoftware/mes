package com.mgs.mes.db;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import java.util.Iterator;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class MongoDao {
	private final DB db;

	public MongoDao(DB db) {
		this.db = db;
	}

	public ObjectId touch(String collectionName, DBObject dbObject){
		db.getCollection(collectionName).save(dbObject);
		return (ObjectId) dbObject.get("_id");
	}

	public DBCursor find(String collectionName, DBObject query){
		return db.getCollection(collectionName).find(query);
	}

	public Optional<DBObject> findOne(String collectionName, DBObject query){
		DBCursor dbObjects = find(collectionName, query);
		Iterator<DBObject> iterator = dbObjects.iterator();
		if (! iterator.hasNext()) return empty();

		DBObject fromDb = iterator.next();
		if (iterator.hasNext()) throw new IllegalStateException("More than one element with the same ID");
		return of(fromDb);
	}
}
