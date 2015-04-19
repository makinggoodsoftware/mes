package com.mgs.mes.model;


import org.bson.types.ObjectId;

@SuppressWarnings("UnusedDeclaration")
public interface OneToOne<T extends Entity> extends Entity{
	public T retrieve() ;

	public String getRefName ();

	public ObjectId getRefId ();
}
