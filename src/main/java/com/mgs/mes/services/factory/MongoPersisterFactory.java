package com.mgs.mes.services.factory;

import com.mgs.mes.db.MongoDao;
import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.entity.data.EntityDataBuilderFactory;
import com.mgs.mes.entity.factory.entity.EntityFactory;
import com.mgs.mes.meta.utils.Entities;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.services.core.EntityPersister;
import com.mgs.mes.services.core.builder.EntityBuilderProvider;
import com.mgs.mes.services.core.reference.EntityReferenceProvider;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mgs.reflection.Reflections;

public class MongoPersisterFactory {
	private final Entities entities;
	private final EntityFactory<EntityData> modelDataEntityFactory;
	private final FieldAccessorParser fieldAccessorParser;
	private final EntityDataBuilderFactory entityDataBuilderFactory;
	private final BeanNamingExpert beanNamingExpert;
	private final Reflections reflections;

	public MongoPersisterFactory(Entities entities, EntityFactory<EntityData> modelDataEntityFactory, FieldAccessorParser fieldAccessorParser, EntityDataBuilderFactory entityDataBuilderFactory, BeanNamingExpert beanNamingExpert, Reflections reflections) {
		this.entities = entities;
		this.modelDataEntityFactory = modelDataEntityFactory;
		this.fieldAccessorParser = fieldAccessorParser;
		this.entityDataBuilderFactory = entityDataBuilderFactory;
		this.beanNamingExpert = beanNamingExpert;
		this.reflections = reflections;
	}

	public <T extends Entity, Z extends EntityBuilder<T>>
	EntityPersister<T, Z> create(MongoDao mongoDao, EntityReferenceProvider entityReferenceProvider, Class<T> persistType, Class<Z> updaterType) {
		EntityBuilderProvider<T, Z> tzEntityBuilderProvider = new EntityBuilderProvider<>(
				entityDataBuilderFactory,
				fieldAccessorParser,
				beanNamingExpert,
				modelDataEntityFactory,
				persistType,
				updaterType,
				reflections,
				entityReferenceProvider);
		return new EntityPersister<>(tzEntityBuilderProvider, mongoDao, entities);
	}
}
