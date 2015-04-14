package com.mgs.config.mes.build;

import com.mgs.config.mes.meta.MetaConfig;
import com.mgs.config.reflection.ReflectionConfig;

public class BuildConfig {
	private final ReflectionConfig reflectionConfig;
	private final MetaConfig metaConfig;
	private final CommonConfig commonConfig = new CommonConfig();

	public BuildConfig(ReflectionConfig reflectionConfig, MetaConfig metaConfig) {
		this.reflectionConfig = reflectionConfig;
		this.metaConfig = metaConfig;
	}

	public EntityDataConfig entityData(){
		return new EntityDataConfig(reflectionConfig, commonConfig);
	}

	public EntityFactoryConfig factories(){
		return new EntityFactoryConfig(entityData(), reflectionConfig, commonConfig, metaConfig);
	}
}
