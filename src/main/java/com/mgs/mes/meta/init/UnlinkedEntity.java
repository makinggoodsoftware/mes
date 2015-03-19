package com.mgs.mes.meta.init;

import com.mgs.mes.build.factory.builder.EntityBuilderFactory;
import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.db.MongoPersister;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.model.Relationships;

public class UnlinkedEntity<T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>> {
	private final EntityRetriever<T> retriever;
	private final MongoPersister<T, Z> persister;
	private final EntityBuilderFactory<T, Z> builder;
	private final EntityDescriptor<T, Z, Y> entityDescriptor;

	public UnlinkedEntity(
			EntityRetriever<T> retriever,
			MongoPersister<T, Z> persister,
			EntityBuilderFactory<T, Z> builder,
			EntityDescriptor<T, Z, Y> entityDescriptor) {
		this.retriever = retriever;
		this.persister = persister;
		this.builder = builder;
		this.entityDescriptor = entityDescriptor;
	}

	public EntityRetriever<T> getRetriever() {
		return retriever;
	}

	public MongoPersister<T, Z> getPersister() {
		return persister;
	}

	public EntityBuilderFactory<T, Z> getBuilder() {
		return builder;
	}

	public EntityDescriptor<T, Z, Y> getEntityDescriptor() {
		return entityDescriptor;
	}
}
