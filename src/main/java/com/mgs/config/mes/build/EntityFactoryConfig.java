package com.mgs.config.mes.build;

import com.mgs.config.ReflectionConfig;
import com.mgs.mes.build.factory.builder.EntityBuilderFactory;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;

public class EntityFactoryConfig {
	private final EntityDataConfig entityDataConfig;
	private final ReflectionConfig reflectionConfig;
	private final CommonConfig commonConfig;

	public EntityFactoryConfig(EntityDataConfig entityDataConfig, ReflectionConfig reflectionConfig, CommonConfig commonConfig) {
		this.entityDataConfig = entityDataConfig;
		this.reflectionConfig = reflectionConfig;
		this.commonConfig = commonConfig;
	}

	public <T extends Entity, Z extends EntityBuilder<T>>
	EntityBuilderFactory entityBuilderFactory(Class<T> modelType, Class<Z> modelBuilderType){
		return new EntityBuilderFactory<>(
				entityDataConfig.builderFactory(),
				reflectionConfig.fieldAccessorParser(),
				reflectionConfig.beanNamingExpert(),
				commonConfig.entityDataEntityFactory(),
				modelType,
				modelBuilderType
		);
	}
}
