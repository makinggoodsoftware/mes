package com.mgs.config.mes.build;

import com.mgs.config.reflection.ReflectionConfig;
import com.mgs.mes.build.data.EntityData;
import com.mgs.mes.build.factory.builder.EntityBuilderFactory;
import com.mgs.mes.build.factory.entity.EntityFactory;
import com.mgs.mes.build.factory.entity.dbo.DBObjectEntityFactory;
import com.mgs.mes.build.factory.entity.entityData.EntityDataEntityFactory;
import com.mgs.mes.build.factory.reference.EntityReferenceFactory;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mongodb.DBObject;

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
	EntityBuilderFactory<T, Z> entityBuilder(Class<T> modelType, Class<Z> modelBuilderType, EntityReferenceFactory entityReferenceFactory){
		return new EntityBuilderFactory<>(
				entityDataConfig.builderFactory(),
				reflectionConfig.fieldAccessorParser(),
				reflectionConfig.beanNamingExpert(),
				commonConfig.entityDataEntityFactory(),
				modelType,
				modelBuilderType,
				reflectionConfig.reflections(),
				entityReferenceFactory);
	}

	public EntityFactory<DBObject> dbObjectEntity(){
		return new DBObjectEntityFactory(
			commonConfig.entityDataEntityFactory(),
			entityDataConfig.factory()
		);
	}

	public EntityFactory<EntityData> entityDataEntity() {
		return new EntityDataEntityFactory();
	}
}
