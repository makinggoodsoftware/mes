package com.mgs.mes.model.relationships;

import com.mgs.mes.model.entity.Entity;
import org.bson.types.ObjectId;

public interface EntityReference<T extends Entity> {
	public T retrieve() ;

	ObjectId getId();
}
