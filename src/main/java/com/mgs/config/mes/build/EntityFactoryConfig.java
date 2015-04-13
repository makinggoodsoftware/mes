package com.mgs.config.mes.build;

import com.mgs.config.mes.meta.MetaConfig;
import com.mgs.config.reflection.ReflectionConfig;
import com.mgs.mes.build.data.EntityData;
import com.mgs.mes.build.factory.builder.EntityBuilderFactory;
import com.mgs.mes.build.factory.builder.RelationshipBuilderFactory;
import com.mgs.mes.build.factory.entity.EntityFactory;
import com.mgs.mes.build.factory.entity.dbo.DBObjectEntityFactory;
import com.mgs.mes.build.factory.entity.entityData.EntityDataEntityFactory;
import com.mgs.mes.build.factory.reference.EntityReferenceFactory;
import com.mgs.mes.build.factory.relationship.RelationshipsFactory;
import com.mgs.mes.context.MongoContextReference;
import com.mgs.mes.model.*;
import com.mongodb.DBObject;

public class EntityFactoryConfig {
	private final EntityDataConfig entityDataConfig;
	private final ReflectionConfig reflectionConfig;
	private final CommonConfig commonConfig;
	private final MetaConfig metaConfig;

	public EntityFactoryConfig(EntityDataConfig entityDataConfig, ReflectionConfig reflectionConfig, CommonConfig commonConfig, MetaConfig metaConfig) {
		this.entityDataConfig = entityDataConfig;
		this.reflectionConfig = reflectionConfig;
		this.commonConfig = commonConfig;
		this.metaConfig = metaConfig;
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

	public <A extends Entity, B extends Entity, T extends Relationship<A, B>, Z extends RelationshipBuilder<A, B, T>>
	RelationshipBuilderFactory<A, B, T, Z> relationshipBuilder(Class<T> relationshipType, Class<Z> relationshipBuilderType, EntityReferenceFactory entityReferenceFactory) {
		return new RelationshipBuilderFactory<> (
				entityDataConfig.builderFactory(),
				reflectionConfig.fieldAccessorParser(),
				reflectionConfig.beanNamingExpert(),
				entityDataEntity(),
				entityReferenceFactory,
				relationshipType,
				relationshipBuilderType,
				reflectionConfig.reflections());
	}

	public <T extends Entity, Y extends Relationships<T>>
	RelationshipsFactory<T, Y> relationships(Class<Y> relationshipsType, MongoContextReference contextReference) {
		return new RelationshipsFactory<>(relationshipsType, contextReference, metaConfig.entities());
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
