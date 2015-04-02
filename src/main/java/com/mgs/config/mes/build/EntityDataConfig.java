package com.mgs.config.mes.build;

import com.mgs.config.reflection.ReflectionConfig;
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
	private final CommonConfig commonConfig;

	public EntityDataConfig(ReflectionConfig reflectionConfig, CommonConfig commonConfig) {
		this.reflectionConfig = reflectionConfig;
		this.commonConfig = commonConfig;
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
		return new DboTransformer(commonConfig.entityDataEntityFactory(), reflectionConfig.beanNamingExpert(), reflectionConfig.fieldAccessorParser());
	}
}
