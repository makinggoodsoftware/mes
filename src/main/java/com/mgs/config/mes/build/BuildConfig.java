package com.mgs.config.mes.build;

import com.mgs.config.ReflectionConfig;

public class BuildConfig {
	private final ReflectionConfig reflectionConfig;
	private final CommonConfig commonConfig = new CommonConfig();

	public BuildConfig(ReflectionConfig reflectionConfig) {
		this.reflectionConfig = reflectionConfig;
	}

	public EntityDataConfig entityData(){
		return new EntityDataConfig(reflectionConfig, commonConfig);
	}

	public EntityFactoryConfig factories(){
		return new EntityFactoryConfig(entityData(), reflectionConfig, commonConfig);
	}
}
