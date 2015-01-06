package com.mgs.mes.model;

import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import java.util.Optional;

public interface MongoEntity {
	public DBObject asDbo();

	public Optional<ObjectId> getId();
}
