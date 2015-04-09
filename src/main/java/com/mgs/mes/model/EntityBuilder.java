package com.mgs.mes.model;

import com.mongodb.DBObject;
import org.bson.types.ObjectId;

public interface EntityBuilder<T extends Entity> {
	Class<T> getEntityType ();

	EntityBuilder<T> withId (ObjectId id);

	T create();

	DBObject asDbo();
}
