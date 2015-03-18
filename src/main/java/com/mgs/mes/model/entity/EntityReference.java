package com.mgs.mes.model.entity;


import org.bson.types.ObjectId;

public interface EntityReference<T extends Entity> extends Entity{
	public T retrieve() ;

	public String getRefName ();

	public ObjectId getRefId ();
}
