package com.mgs.config.mes.build;

import com.mgs.config.ReflectionConfig;
import com.mgs.mes.build.data.EntityDataBuilderFactory;
import com.mgs.mes.build.data.EntityDataFactory;
import com.mgs.mes.build.data.transformer.DboTransformer;
import com.mgs.mes.build.data.transformer.EntityDataTransformer;
import com.mgs.mes.build.data.transformer.FieldAccessorMapTransformer;
import com.mgs.reflection.FieldAccessor;
import com.mongodb.DBObject;

import java.util.Map;

public class EntityDataConfig {
	private final ReflectionConfig reflectionConfig;
	private final EntityFactoryConfig entityFactoryConfig;

	public EntityDataConfig(ReflectionConfig reflectionConfig, EntityFactoryConfig entityFactoryConfig) {
		this.reflectionConfig = reflectionConfig;
		this.entityFactoryConfig = entityFactoryConfig;
	}

	public EntityDataBuilderFactory entityDataBuilderFactory (){
		return new EntityDataBuilderFactory(entityDataFactory(), reflectionConfig.beanNamingExpert(), reflectionConfig.fieldAccessorParser());
	}

	private EntityDataFactory entityDataFactory() {
		return new EntityDataFactory(dboEntityDataTransformer(), mapEntityDataTransformer());
	}

	private EntityDataTransformer<Map<FieldAccessor, Object>> mapEntityDataTransformer() {
		return new FieldAccessorMapTransformer();
	}

	private EntityDataTransformer<DBObject> dboEntityDataTransformer() {
		return new DboTransformer(entityFactoryConfig.entityDataEntityFactory(), reflectionConfig.beanNamingExpert(), reflectionConfig.fieldAccessorParser());
	}
}
