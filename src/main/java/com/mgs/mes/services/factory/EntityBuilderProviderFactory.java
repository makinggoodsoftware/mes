package com.mgs.mes.services.factory;

import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.entity.data.EntityDataBuilderFactory;
import com.mgs.mes.entity.factory.entity.EntityFactory;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.services.core.builder.EntityBuilderProvider;
import com.mgs.mes.services.core.reference.EntityReferenceProvider;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mgs.reflection.Reflections;

public class EntityBuilderProviderFactory {
	private final EntityFactory<EntityData> modelDataEntityFactory;
	private final FieldAccessorParser fieldAccessorParser;
	private final EntityDataBuilderFactory entityDataBuilderFactory;
	private final BeanNamingExpert beanNamingExpert;
	private final Reflections reflections;

	public EntityBuilderProviderFactory(EntityFactory<EntityData> modelDataEntityFactory, FieldAccessorParser fieldAccessorParser, EntityDataBuilderFactory entityDataBuilderFactory, BeanNamingExpert beanNamingExpert, Reflections reflections) {
		this.modelDataEntityFactory = modelDataEntityFactory;
		this.fieldAccessorParser = fieldAccessorParser;
		this.entityDataBuilderFactory = entityDataBuilderFactory;
		this.beanNamingExpert = beanNamingExpert;
		this.reflections = reflections;
	}

	public <T extends Entity, Z extends EntityBuilder<T>>
	EntityBuilderProvider<T, Z> builder(EntityReferenceProvider entityReferenceProvider, Class<? extends T> typeOfModel, Class<Z> typeOfBuilder) {
		//noinspection unchecked
		return new EntityBuilderProvider<>(
				entityDataBuilderFactory,
				fieldAccessorParser,
				beanNamingExpert,
				modelDataEntityFactory,
				(Class<T>) typeOfModel,
				typeOfBuilder,
				reflections,
				entityReferenceProvider);
	}
}
