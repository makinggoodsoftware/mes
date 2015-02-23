package com.mgs.mes.model.entity;

import com.mgs.mes.model.relationships.EntityReference;

public interface Relationship<A extends Entity, B extends Entity> extends Entity {
	EntityReference<A> getLeft ();

	EntityReference<B> getRight ();
}
