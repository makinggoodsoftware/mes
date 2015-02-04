package com.mgs.mes.factory;

import com.mgs.mes.db.MongoPersister;
import com.mgs.mes.db.MongoRetriever;
import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.model.MongoEntityBuilder;
import com.mgs.mes.model.MongoRelationships;
import com.mgs.mes.model.builder.ModelBuilderFactory;
import com.mgs.mes.model.relationships.ModelRelationshipsBuilderFactory;

public class MongoManager <T extends MongoEntity, Z extends MongoEntityBuilder<T>, Y extends MongoRelationships<T>> {
	private final MongoRetriever<T> retriever;
	private final MongoPersister<T, Z> persister;
	private final ModelBuilderFactory<T, Z> builder;
	private final ModelRelationshipsBuilderFactory<T, Y> relationshipsBuilderFactory;

	public MongoManager(
			MongoRetriever<T> retriever,
			MongoPersister<T, Z> persister,
			ModelBuilderFactory<T, Z> builder,
			ModelRelationshipsBuilderFactory<T, Y> relationshipsBuilderFactory
	) {
		this.retriever = retriever;
		this.persister = persister;
		this.builder = builder;
		this.relationshipsBuilderFactory = relationshipsBuilderFactory;
	}

	public MongoRetriever<T> getRetriever() {
		return retriever;
	}

	public MongoPersister<T, Z> getPersister() {
		return persister;
	}

	public Y relationshipFrom(T from){
		return relationshipsBuilderFactory.from(from);
	}

	public ModelBuilderFactory<T, Z> getBuilder() {
		return builder;
	}
}
