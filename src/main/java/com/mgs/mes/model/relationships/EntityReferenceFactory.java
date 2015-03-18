package com.mgs.mes.model.relationships;

import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.model.data.EntityData;
import com.mgs.mes.model.data.EntityDataBuilderFactory;
import com.mgs.mes.model.entity.Entity;
import com.mgs.mes.model.entity.EntityReference;
import com.mgs.mes.utils.Entities;

import static java.lang.reflect.Proxy.newProxyInstance;

public class EntityReferenceFactory {
	private final EntityDataBuilderFactory entityDataBuilderFactory;
	private final Entities entities;

	public EntityReferenceFactory(EntityDataBuilderFactory entityDataBuilderFactory, Entities entities) {
		this.entityDataBuilderFactory = entityDataBuilderFactory;
		this.entities = entities;
	}

	public <T extends Entity> EntityReference<T> newReference(T from, EntityRetriever retriever) {
		if (!from.getId().isPresent()) throw new IllegalStateException("Can't create a reference to an empty object");

		EntityData data = entityDataBuilderFactory.empty(EntityReference.class).
				with("refName", entities.collectionName(from.getClass())).
				with("refId", from.getId().get()).
				build();

		//noinspection unchecked
		return (EntityReference<T>) newProxyInstance(
				EntityReferenceFactory.class.getClassLoader(),
				new Class[]{EntityReference.class},
				new EntityReferenceCallInterceptor(data, retriever)
		);

	}
}