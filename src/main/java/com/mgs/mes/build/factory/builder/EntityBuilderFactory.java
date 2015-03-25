package com.mgs.mes.build.factory.builder;

import com.mgs.mes.build.data.EntityData;
import com.mgs.mes.build.data.EntityDataBuilder;
import com.mgs.mes.build.data.EntityDataBuilderFactory;
import com.mgs.mes.build.factory.entity.EntityFactory;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;

import java.lang.reflect.Proxy;

public class EntityBuilderFactory<T extends Entity, Z extends EntityBuilder<T>> {
	private final EntityDataBuilderFactory entityDataBuilderFactory;
	private final FieldAccessorParser fieldAccessorParser;
	private final BeanNamingExpert beanNamingExpert;
	private final Class<T> modelType;
	private final Class<Z> modelBuilderType;
	private final EntityFactory<EntityData> entityFactory;

	public EntityBuilderFactory(EntityDataBuilderFactory entityDataBuilderFactory, FieldAccessorParser fieldAccessorParser, BeanNamingExpert beanNamingExpert, EntityFactory<EntityData> entityFactory, Class<T> modelType, Class<Z> modelBuilderType) {
		this.entityDataBuilderFactory = entityDataBuilderFactory;
		this.fieldAccessorParser = fieldAccessorParser;
		this.beanNamingExpert = beanNamingExpert;
		this.modelType = modelType;
		this.modelBuilderType = modelBuilderType;
		this.entityFactory = entityFactory;
	}

	public Z newEntityBuilder() {
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
				new EntityBuilderCallInterceptor<>(fieldAccessorParser, beanNamingExpert, modelType, entityDataBuilder, entityFactory)
		);
	}
}