package com.mgs.mes.model;

import org.bson.types.ObjectId;

public interface EntityBuilder<T extends Entity> {
	EntityBuilder<T> withId (ObjectId id);

	T create();
}
