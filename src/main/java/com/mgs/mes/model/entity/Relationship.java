package com.mgs.mes.model.entity;

public interface Relationship<A extends Entity, B extends Entity> extends Entity {
	EntityReference<A> getLeft ();

	EntityReference<B> getRight ();
}
