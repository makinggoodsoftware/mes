package com.mgs.config.mes.entity;

import com.mgs.config.reflection.ReflectionConfig;
import com.mgs.mes.entity.data.EntityDataBuilderFactory;
import com.mgs.mes.entity.data.EntityDataFactory;
import com.mgs.mes.entity.data.transformer.DboTransformer;
import com.mgs.mes.entity.data.transformer.EntityDataTransformer;
import com.mgs.mes.entity.data.transformer.FieldAccessorMapTransformer;
import com.mgs.reflection.FieldAccessor;
import com.mongodb.DBObject;

import java.util.Map;

public class EntityDataConfig {
	private final ReflectionConfig reflectionConfig;
	private final CommonEntityConfig commonEntityConfig;

	public EntityDataConfig(ReflectionConfig reflectionConfig, CommonEntityConfig commonEntityConfig) {
		this.reflectionConfig = reflectionConfig;
		this.commonEntityConfig = commonEntityConfig;
	}

	public EntityDataBuilderFactory builderFactory(){
		return new EntityDataBuilderFactory(factory(), reflectionConfig.beanNamingExpert(), reflectionConfig.fieldAccessorParser());
	}

	public EntityDataFactory factory() {
		return new EntityDataFactory(dboEntityDataTransformer(), mapEntityDataTransformer());
	}

	private EntityDataTransformer<Map<FieldAccessor, Object>> mapEntityDataTransformer() {
		return new FieldAccessorMapTransformer();
	}

	private EntityDataTransformer<DBObject> dboEntityDataTransformer() {
		return new DboTransformer(commonEntityConfig.entityDataEntityFactory(), reflectionConfig.beanNamingExpert(), reflectionConfig.fieldAccessorParser(), reflectionConfig.reflections());
	}
}
