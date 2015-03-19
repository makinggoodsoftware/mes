package com.mgs.mes.model;

public interface Relationship<A extends Entity, B extends Entity> extends Entity {
	EntityReference<A> getLeft ();

	EntityReference<B> getRight ();
}
