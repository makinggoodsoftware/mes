package com.mgs.mes.factory;

import com.mgs.mes.db.MongoPersister;
import com.mgs.mes.db.MongoRetriever;
import com.mgs.mes.model.builder.EntityBuilderFactory;
import com.mgs.mes.model.entity.Entity;
import com.mgs.mes.model.entity.EntityBuilder;
import com.mgs.mes.model.entity.Relationships;

public class UnlinkedEntityDescriptor<T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>> {
	private final MongoRetriever<T> retriever;
	private final MongoPersister<T, Z> persister;
	private final EntityBuilderFactory<T, Z> builder;
	private final Class<Y> relationshipsType;
	private final Class<Z> builderType;

	public UnlinkedEntityDescriptor(
			MongoRetriever<T> retriever,
			MongoPersister<T, Z> persister,
			EntityBuilderFactory<T, Z> builder,
			Class<Y> relationshipsType,
			Class<Z>  builderType
		) {
		this.retriever = retriever;
		this.persister = persister;
		this.builder = builder;
		this.relationshipsType = relationshipsType;
		this.builderType = builderType;
	}

	public MongoRetriever<T> getRetriever() {
		return retriever;
	}

	public MongoPersister<T, Z> getPersister() {
		return persister;
	}

	public EntityBuilderFactory<T, Z> getBuilder() {
		return builder;
	}

	public Class<Y> getRelationshipsType() {
		return relationshipsType;
	}

	public Class<Z> getBuilderType() {
		return builderType;
	}
}
