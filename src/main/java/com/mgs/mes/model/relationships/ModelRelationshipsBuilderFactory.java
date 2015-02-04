package com.mgs.mes.model.relationships;

import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.model.MongoRelationships;

public class ModelRelationshipsBuilderFactory<T extends MongoEntity, Y extends MongoRelationships<T>> {
	public Y from(T from) {
		return null;
	}
}
