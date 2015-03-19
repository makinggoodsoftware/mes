package com.mgs.mes.model;

public interface RelationshipBuilder<T extends Entity, A extends Entity, B extends Entity> extends EntityBuilder<T> {
	RelationshipBuilder<T, A, B> withLeft(EntityReference<A> left);

	RelationshipBuilder<T, A, B> withRight(EntityReference<B> right);
}
