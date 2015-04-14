package com.mgs.mes.services.factory;

import com.mgs.mes.entity.data.EntityDataBuilderFactory;
import com.mgs.mes.meta.utils.Entities;
import com.mgs.mes.services.core.EntityRetriever;
import com.mgs.mes.services.core.reference.EntityReferenceProvider;

import java.util.Map;

public class EntityReferenceProviderFactory {
	private final EntityDataBuilderFactory entityDataBuilderFactory;
	private final Entities entities;

	public EntityReferenceProviderFactory(EntityDataBuilderFactory entityDataBuilderFactory, Entities entities) {
		this.entityDataBuilderFactory = entityDataBuilderFactory;
		this.entities = entities;
	}

	public EntityReferenceProvider create(Map<Class, EntityRetriever> retrieverMap){
		return new EntityReferenceProvider(entityDataBuilderFactory, entities, retrieverMap);
	}
}
