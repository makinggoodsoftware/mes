package com.mgs.mes.model;

public interface RelationshipBuilder<A extends Entity, B extends Entity, T extends Relationship<A, B>> extends EntityBuilder<T> {
	RelationshipBuilder<A, B, T> withLeft(EntityReference<A> left);

	RelationshipBuilder<A, B, T> withRight(EntityReference<B> right);
}
