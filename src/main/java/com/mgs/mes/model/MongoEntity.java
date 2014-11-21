package com.mgs.mes.model;

import com.mongodb.DBObject;

public interface MongoEntity {
	public DBObject asDbo();
}
