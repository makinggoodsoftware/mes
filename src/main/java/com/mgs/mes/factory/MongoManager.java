package com.mgs.mes.factory;

import com.mgs.mes.db.MongoPersister;
import com.mgs.mes.db.MongoRetriever;
import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.model.MongoEntityBuilder;
import com.mgs.mes.model.builder.ModelBuilderFactory;

public class MongoManager <T extends MongoEntity, Z extends MongoEntityBuilder<T>> {
	private final MongoRetriever<T> retriever;
	private final MongoPersister<T, Z> persister;
	private final ModelBuilderFactory<T, Z> builder;

	public MongoManager(MongoRetriever<T> retriever, MongoPersister<T, Z> persister, ModelBuilderFactory<T, Z> builder) {
		this.retriever = retriever;
		this.persister = persister;
		this.builder = builder;
	}

	public MongoRetriever<T> getRetriever() {
		return retriever;
	}

	public MongoPersister<T, Z> getPersister() {
		return persister;
	}

	public ModelBuilderFactory<T, Z> getBuilder() {
		return builder;
	}
}
