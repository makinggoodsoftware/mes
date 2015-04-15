package com.mgs.mes.context;

import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.services.core.EntityPersister;
import com.mgs.mes.services.core.EntityRetriever;
import com.mgs.mes.services.core.builder.EntityBuilderProvider;

import java.util.function.Function;

public class MongoManager <T extends Entity, Z extends EntityBuilder<T>> {
	private final EntityRetriever<T> retriever;
	private final EntityPersister<T, Z> persister;
	private final EntityBuilderProvider<T, Z> builder;

	public MongoManager(
			EntityRetriever<T> retriever,
			EntityPersister<T, Z> persister,
			EntityBuilderProvider<T, Z> builder
	) {
		this.retriever = retriever;
		this.persister = persister;
		this.builder = builder;
	}

	public EntityRetriever<T> getRetriever() {
		return retriever;
	}

	public EntityPersister<T, Z> getPersister() {
		return persister;
	}

	public EntityBuilderProvider<T, Z> getBuilder() {
		return builder;
	}

	public Z newEntity() {
		return getBuilder().newEntity();
	}

	public T createAndPersist(Function<Z, Z> fieldEnricher) {
		Z toSave = fieldEnricher.apply(newEntity());
		return getPersister().create(toSave);
	}
}
