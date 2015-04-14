package com.mgs.mes.services.core.builder;

import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.entity.data.EntityDataBuilder;
import com.mgs.mes.entity.data.EntityDataBuilderFactory;
import com.mgs.mes.entity.factory.entity.EntityFactory;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.services.core.reference.EntityReferenceProvider;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mgs.reflection.Reflections;

import java.lang.reflect.Proxy;

public class EntityBuilderProvider<T extends Entity, Z extends EntityBuilder<T>> {
	private final EntityDataBuilderFactory entityDataBuilderFactory;
	private final FieldAccessorParser fieldAccessorParser;
	private final BeanNamingExpert beanNamingExpert;
	private final Class<T> modelType;
	private final Class<Z> modelBuilderType;
	private final EntityFactory<EntityData> entityFactory;
	private final Reflections reflections;
	private final EntityReferenceProvider entityReferenceProvider;

	public EntityBuilderProvider(EntityDataBuilderFactory entityDataBuilderFactory, FieldAccessorParser fieldAccessorParser, BeanNamingExpert beanNamingExpert, EntityFactory<EntityData> entityFactory, Class<T> modelType, Class<Z> modelBuilderType, Reflections reflections, EntityReferenceProvider entityReferenceProvider) {
		this.entityDataBuilderFactory = entityDataBuilderFactory;
		this.fieldAccessorParser = fieldAccessorParser;
		this.beanNamingExpert = beanNamingExpert;
		this.modelType = modelType;
		this.modelBuilderType = modelBuilderType;
		this.entityFactory = entityFactory;
		this.reflections = reflections;
		this.entityReferenceProvider = entityReferenceProvider;
	}

	public Z create() {
		return newProxyInstance(entityDataBuilderFactory.empty(modelType));
	}

	public Z update(T baseLine) {
		return newProxyInstance(entityDataBuilderFactory.from(modelType, baseLine));
	}

	private Z newProxyInstance(EntityDataBuilder entityDataBuilder) {
		//noinspection unchecked
		return (Z) Proxy.newProxyInstance(
				this.getClass().getClassLoader(),
				new Class[]{modelBuilderType},
				new EntityBuilderCallInterceptor<>(
						fieldAccessorParser,
						beanNamingExpert,
						modelType,
						entityDataBuilder,
						entityFactory,
						reflections,
						entityReferenceProvider
				)
		);
	}
}
