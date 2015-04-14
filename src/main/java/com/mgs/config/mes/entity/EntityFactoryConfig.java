package com.mgs.config.mes.entity;

import com.mgs.config.reflection.ReflectionConfig;
import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.entity.factory.entity.EntityFactory;
import com.mgs.mes.entity.factory.entity.dbo.DBObjectEntityFactory;
import com.mgs.mes.entity.factory.entity.entityData.EntityDataEntityFactory;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.services.core.builder.EntityBuilderProvider;
import com.mgs.mes.services.core.reference.EntityReferenceProvider;
import com.mongodb.DBObject;

public class EntityFactoryConfig {
	private final EntityDataConfig entityDataConfig;
	private final ReflectionConfig reflectionConfig;
	private final CommonEntityConfig commonEntityConfig;

	public EntityFactoryConfig(EntityDataConfig entityDataConfig, ReflectionConfig reflectionConfig, CommonEntityConfig commonEntityConfig) {
		this.entityDataConfig = entityDataConfig;
		this.reflectionConfig = reflectionConfig;
		this.commonEntityConfig = commonEntityConfig;
	}

	public <T extends Entity, Z extends EntityBuilder<T>>
	EntityBuilderProvider<T, Z> entityBuilder(Class<T> modelType, Class<Z> modelBuilderType, EntityReferenceProvider entityReferenceProvider){
		return new EntityBuilderProvider<>(
				entityDataConfig.builderFactory(),
				reflectionConfig.fieldAccessorParser(),
				reflectionConfig.beanNamingExpert(),
				commonEntityConfig.entityDataEntityFactory(),
				modelType,
				modelBuilderType,
				reflectionConfig.reflections(),
				entityReferenceProvider);
	}

	public EntityFactory<DBObject> dbObjectEntity(){
		return new DBObjectEntityFactory(
			commonEntityConfig.entityDataEntityFactory(),
			entityDataConfig.factory()
		);
	}

	public EntityFactory<EntityData> entityDataEntity() {
		return new EntityDataEntityFactory();
	}
}
