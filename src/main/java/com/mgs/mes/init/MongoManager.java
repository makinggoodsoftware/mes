package com.mgs.mes.init;

import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.db.MongoPersister;
import com.mgs.mes.model.builder.EntityBuilderFactory;
import com.mgs.mes.model.entity.Entity;
import com.mgs.mes.model.entity.EntityBuilder;
import com.mgs.mes.model.entity.Relationships;
import com.mgs.mes.model.relationships.RelationshipsFactory;

public class MongoManager <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>> {
	private final EntityRetriever<T> retriever;
	private final MongoPersister<T, Z> persister;
	private final EntityBuilderFactory<T, Z> builder;
	private final RelationshipsFactory<T, Y> relationshipsFactory;

	public MongoManager(
			EntityRetriever<T> retriever,
			MongoPersister<T, Z> persister,
			EntityBuilderFactory<T, Z> builder,
			RelationshipsFactory<T, Y> relationshipsFactory
	) {
		this.retriever = retriever;
		this.persister = persister;
		this.builder = builder;
		this.relationshipsFactory = relationshipsFactory;
	}

	public EntityRetriever<T> getRetriever() {
		return retriever;
	}

	public MongoPersister<T, Z> getPersister() {
		return persister;
	}

	public Y relationshipFrom(T from){
		return relationshipsFactory.from(from);
	}

	public EntityBuilderFactory<T, Z> getBuilder() {
		return builder;
	}

	void setContext(MongoContext context) {
		 relationshipsFactory.setContext (context);
	}
}
