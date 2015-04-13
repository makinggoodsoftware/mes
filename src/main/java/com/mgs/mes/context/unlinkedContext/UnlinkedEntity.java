package com.mgs.mes.context.unlinkedContext;

import com.mgs.mes.context.EntityDescriptor;
import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.model.Relationships;

public class UnlinkedEntity<T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>> {
	private final EntityRetriever<T> retriever;
	private final EntityDescriptor<T, Z, Y> entityDescriptor;

	public UnlinkedEntity(
			EntityRetriever<T> retriever,
			EntityDescriptor<T, Z, Y> entityDescriptor) {
		this.retriever = retriever;
		this.entityDescriptor = entityDescriptor;
	}

	public EntityRetriever<T> getRetriever() {
		return retriever;
	}

	public EntityDescriptor<T, Z, Y> getEntityDescriptor() {
		return entityDescriptor;
	}
}
