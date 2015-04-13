package com.mgs.mes.context;

import com.mgs.mes.build.factory.builder.EntityBuilderFactory;
import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.db.MongoPersister;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;

import java.util.function.Function;

public class MongoManager <T extends Entity, Z extends EntityBuilder<T>> {
	private final EntityRetriever<T> retriever;
	private final MongoPersister<T, Z> persister;
	private final EntityBuilderFactory<T, Z> builder;

	public MongoManager(
			EntityRetriever<T> retriever,
			MongoPersister<T, Z> persister,
			EntityBuilderFactory<T, Z> builder
	) {
		this.retriever = retriever;
		this.persister = persister;
		this.builder = builder;
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

	public Z newEntity() {
		return getBuilder().newEntityBuilder();
	}

	public T createAndPersist(Function<Z, Z> fieldEnricher) {
		Z toSave = fieldEnricher.apply(newEntity());
		return getPersister().create(toSave);
	}
}
