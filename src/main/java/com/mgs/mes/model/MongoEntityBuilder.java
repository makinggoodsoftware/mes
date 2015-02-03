package com.mgs.mes.model;

import org.bson.types.ObjectId;

public interface MongoEntityBuilder<T extends MongoEntity> {
	MongoEntityBuilder<T> withId (ObjectId id);

	T create();
}
