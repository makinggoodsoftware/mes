package com.mgs.mes.v2.config;

import com.mgs.config.reflection.ReflectionConfig;
import com.mgs.mes.v2.entity.property.manager.EntityManager;
import com.mgs.mes.v2.entity.property.manager.OneToManyManager;
import com.mgs.mes.v2.entity.property.manager.OneToOneManager;

public class ManagerConfig {
	private final ContextConfig contextConfig;
	private final ReflectionConfig reflectionConfig;

	public ManagerConfig(ContextConfig contextConfig, ReflectionConfig reflectionConfig) {
		this.contextConfig = contextConfig;
		this.reflectionConfig = reflectionConfig;
	}

	public OneToOneManager oneToOne(){
		return new OneToOneManager(contextConfig.retrievers());
	}

	public OneToManyManager oneToMany(){
		return new OneToManyManager();
	}

	public EntityManager entityManager(){
		return new EntityManager(reflectionConfig.reflections());
	}
}
