package com.mgs.mes.model.entity;

import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import java.util.Optional;

public interface Entity {
	public DBObject asDbo();

	public Optional<ObjectId> getId();
}
