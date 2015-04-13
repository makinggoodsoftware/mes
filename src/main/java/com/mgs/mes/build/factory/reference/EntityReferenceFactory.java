package com.mgs.mes.build.factory.reference;

import com.mgs.mes.build.data.EntityData;
import com.mgs.mes.build.data.EntityDataBuilderFactory;
import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.meta.utils.Entities;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityReference;

import java.util.Map;

import static java.lang.reflect.Proxy.newProxyInstance;

public class EntityReferenceFactory {
	private final EntityDataBuilderFactory entityDataBuilderFactory;
	private final Entities entities;
	private final Map<Class, EntityRetriever> retrieverMap;

	public EntityReferenceFactory(EntityDataBuilderFactory entityDataBuilderFactory, Entities entities, Map<Class, EntityRetriever> retrieverMap) {
		this.entityDataBuilderFactory = entityDataBuilderFactory;
		this.entities = entities;
		this.retrieverMap = retrieverMap;
	}

	public <T extends Entity> EntityReference<T> newReference(T from) {
		if (!from.getId().isPresent()) throw new IllegalStateException("Can't create a reference to an object that has not yet been persisted " + from);

		EntityData data = entityDataBuilderFactory.empty(EntityReference.class).
				with("refName", entities.collectionName(from.getClass())).
				with("refId", from.getId().get()).
				build();

		Class<Entity> entityType = entities.findBaseType(from.getClass(), Entity.class);
		//noinspection unchecked
		EntityRetriever<T> retriever = retrieverMap.get(entityType);


		//noinspection unchecked
		return (EntityReference<T>) newProxyInstance(
				EntityReferenceFactory.class.getClassLoader(),
				new Class[]{EntityReference.class},
				new EntityReferenceCallInterceptor(data, retriever)
		);

	}
}