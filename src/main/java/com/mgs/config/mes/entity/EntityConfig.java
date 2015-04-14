package com.mgs.config.mes.entity;

import com.mgs.config.reflection.ReflectionConfig;

public class EntityConfig {
	private final ReflectionConfig reflectionConfig;
	private final CommonEntityConfig commonEntityConfig = new CommonEntityConfig();

	public EntityConfig(ReflectionConfig reflectionConfig) {
		this.reflectionConfig = reflectionConfig;
	}

	public EntityDataConfig entityData(){
		return new EntityDataConfig(reflectionConfig, commonEntityConfig);
	}

	public EntityFactoryConfig factories(){
		return new EntityFactoryConfig(entityData(), reflectionConfig, commonEntityConfig);
	}
}
