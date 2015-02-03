package com.mgs.mes.db;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

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
}
