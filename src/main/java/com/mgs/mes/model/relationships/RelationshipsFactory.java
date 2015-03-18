package com.mgs.mes.model.relationships;

import com.mgs.mes.init.MongoContext;
import com.mgs.mes.init.MongoContextReference;
import com.mgs.mes.model.entity.Entity;
import com.mgs.mes.model.entity.Relationships;
import com.mgs.mes.utils.Entities;

import static java.lang.reflect.Proxy.newProxyInstance;

public class RelationshipsFactory<T extends Entity, Y extends Relationships<T>> {
	private final Class<Y> relationshipType;
	private final MongoContextReference contextReference;
	private final Entities entities;

	public RelationshipsFactory(
			Class<Y> relationshipType,
			MongoContextReference contextReference,
			Entities entities) {
		this.relationshipType = relationshipType;
		this.contextReference = contextReference;
		this.entities = entities;
	}

	public Y from(T from) {
		//noinspection unchecked
		Y proxy = (Y) newProxyInstance(
				RelationshipsFactory.class.getClassLoader(),
				new Class[]{relationshipType},
				new RelationshipsCallInterceptor(contextReference.get(), entities)
		);
		//noinspection unchecked
		return (Y) proxy.from(from);
	}

	public void setContext(MongoContext context) {
		contextReference.set(context);
	}
}
