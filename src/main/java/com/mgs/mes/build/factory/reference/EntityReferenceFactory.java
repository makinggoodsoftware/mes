package com.mgs.mes.build.factory.reference;

import com.mgs.mes.build.data.EntityData;
import com.mgs.mes.build.data.EntityDataBuilderFactory;
import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.meta.utils.Entities;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityReference;

import static java.lang.reflect.Proxy.newProxyInstance;

public class EntityReferenceFactory {
	private final EntityDataBuilderFactory entityDataBuilderFactory;
	private final Entities entities;

	public EntityReferenceFactory(EntityDataBuilderFactory entityDataBuilderFactory, Entities entities) {
		this.entityDataBuilderFactory = entityDataBuilderFactory;
		this.entities = entities;
	}

	public <T extends Entity> EntityReference<T> newReference(T from, EntityRetriever retriever) {
		if (!from.getId().isPresent()) throw new IllegalStateException("Can't create a reference to an object that has not yet been persisted " + from);

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