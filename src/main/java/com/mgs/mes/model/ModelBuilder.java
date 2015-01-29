package com.mgs.mes.model;

import org.bson.types.ObjectId;

public interface ModelBuilder<T extends MongoEntity> {
	ModelBuilder<T> withId (ObjectId id);

	T create();
}
