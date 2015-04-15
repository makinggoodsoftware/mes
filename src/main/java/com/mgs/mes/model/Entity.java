package com.mgs.mes.model;

import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import java.util.Optional;

public interface Entity {
	public DBObject asDbo();

	public Optional<ObjectId> getId();

	boolean dataEquals(Entity entity);
}
