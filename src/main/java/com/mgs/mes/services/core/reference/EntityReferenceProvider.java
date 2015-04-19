package com.mgs.mes.services.core.reference;

import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.entity.data.EntityDataBuilderFactory;
import com.mgs.mes.meta.utils.Entities;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.OneToOne;
import com.mgs.mes.services.core.EntityRetriever;

import java.util.Map;

import static java.lang.reflect.Proxy.newProxyInstance;

public class EntityReferenceProvider {
	private final EntityDataBuilderFactory entityDataBuilderFactory;
	private final Entities entities;
	private final Map<Class, EntityRetriever> retrieverMap;

	public EntityReferenceProvider(EntityDataBuilderFactory entityDataBuilderFactory, Entities entities, Map<Class, EntityRetriever> retrieverMap) {
		this.entityDataBuilderFactory = entityDataBuilderFactory;
		this.entities = entities;
		this.retrieverMap = retrieverMap;
	}

	public <T extends Entity> OneToOne<T> newReference(T from) {
		if (!from.getId().isPresent()) throw new IllegalStateException("Can't create a reference to an object that has not yet been persisted " + from);

		EntityData data = entityDataBuilderFactory.empty(OneToOne.class).
				with("refName", entities.collectionName(from.getClass())).
				with("refId", from.getId().get()).
				build();

		Class<Entity> entityType = entities.findBaseType(from.getClass(), Entity.class);
		//noinspection unchecked
		EntityRetriever<T> retriever = retrieverMap.get(entityType);


		//noinspection unchecked
		return (OneToOne<T>) newProxyInstance(
				EntityReferenceProvider.class.getClassLoader(),
				new Class[]{OneToOne.class},
				new OneToOneCallInterceptor(data, retriever)
		);

	}
}