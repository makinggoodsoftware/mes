package com.mgs.mes.init;

import com.mgs.mes.model.Validator;
import com.mgs.mes.model.data.EntityData;
import com.mgs.mes.model.data.EntityDataBuilderFactory;
import com.mgs.mes.model.data.EntityDataFactory;
import com.mgs.mes.model.data.transformer.DboTransformer;
import com.mgs.mes.model.data.transformer.FieldAccessorMapTransformer;
import com.mgs.mes.model.factory.EntityFactory;
import com.mgs.mes.model.factory.dbo.DBObjectEntityFactory;
import com.mgs.mes.model.factory.entityData.EntityDataEntityFactory;
import com.mgs.mes.model.relationships.EntityReferenceFactory;
import com.mgs.mes.utils.Entities;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mgs.reflection.Reflections;
import com.mongodb.DBObject;

public class MongoInternalDependencies {
	private final Validator validator;
	private final EntityDataBuilderFactory entityDataBuilderFactory;
	private final FieldAccessorParser fieldAccessorParser;
	private final EntityFactory<DBObject> dboEntityFactory;
	private final EntityFactory<EntityData> entityDataEntityFactory;
	private final BeanNamingExpert beanNamingExpert;
	private final Entities entities;
	private final MongoContextFactory mongoContextFactory;
	private final UnlinkedMongoContextFactory unlinkedMongoContextFactory;
	private final EntityReferenceFactory entityReferenceFactory;

	public static MongoInternalDependencies init () {
		EntityFactory<EntityData> modelDataEntityFactory = new EntityDataEntityFactory();
		BeanNamingExpert beanNamingExpert = new BeanNamingExpert();
		FieldAccessorParser fieldAccessorParser = new FieldAccessorParser(beanNamingExpert);

		Reflections reflections = new Reflections();
		DboTransformer dboModelDataTransformer = new DboTransformer(modelDataEntityFactory, beanNamingExpert, fieldAccessorParser);
		FieldAccessorMapTransformer mapModelDataTransformer = new FieldAccessorMapTransformer();
		EntityDataFactory entityDataFactory = new EntityDataFactory(dboModelDataTransformer, mapModelDataTransformer);

		Validator validator = new Validator(reflections, fieldAccessorParser);
		EntityDataBuilderFactory entityDataBuilderFactory = new EntityDataBuilderFactory(entityDataFactory, beanNamingExpert, fieldAccessorParser);
		EntityFactory<DBObject> dbObjectEntityFactory = new DBObjectEntityFactory(modelDataEntityFactory, entityDataFactory);
		Entities entities = new Entities();

		MongoContextFactory mongoContextFactory = new MongoContextFactory(entities);
		UnlinkedMongoContextFactory unlinkedMongoContextFactory = new UnlinkedMongoContextFactory();

		EntityReferenceFactory entityReferenceFactory = new EntityReferenceFactory(entityDataBuilderFactory, entities);

		return new MongoInternalDependencies(
				dbObjectEntityFactory,
				validator,
				entityDataBuilderFactory,
				fieldAccessorParser,
				modelDataEntityFactory,
				beanNamingExpert,
				entities,
				mongoContextFactory,
				unlinkedMongoContextFactory,
				entityReferenceFactory);
	}

	private MongoInternalDependencies(EntityFactory<DBObject> dboEntityFactory, Validator validator, EntityDataBuilderFactory entityDataBuilderFactory, FieldAccessorParser fieldAccessorParser, EntityFactory<EntityData> entityDataEntityFactory, BeanNamingExpert beanNamingExpert, Entities entities, MongoContextFactory mongoContextFactory, UnlinkedMongoContextFactory unlinkedMongoContextFactory, EntityReferenceFactory entityReferenceFactory) {
		this.dboEntityFactory = dboEntityFactory;
		this.validator = validator;
		this.entityDataBuilderFactory = entityDataBuilderFactory;
		this.fieldAccessorParser = fieldAccessorParser;
		this.entityDataEntityFactory = entityDataEntityFactory;
		this.beanNamingExpert = beanNamingExpert;
		this.entities = entities;
		this.mongoContextFactory = mongoContextFactory;
		this.unlinkedMongoContextFactory = unlinkedMongoContextFactory;
		this.entityReferenceFactory = entityReferenceFactory;
	}

	public EntityFactory<DBObject> getDboEntityFactory() {
		return dboEntityFactory;
	}

	public Validator getValidator() {
		return validator;
	}

	public EntityDataBuilderFactory getEntityDataBuilderFactory() {
		return entityDataBuilderFactory;
	}

	public FieldAccessorParser getFieldAccessorParser() {
		return fieldAccessorParser;
	}

	public EntityFactory<EntityData> getEntityDataEntityFactory() {
		return entityDataEntityFactory;
	}

	public BeanNamingExpert getBeanNamingExpert() {
		return beanNamingExpert;
	}

	public Entities getEntities() {
		return entities;
	}

	public MongoContextFactory getMongoContextFactory() {
		return mongoContextFactory;
	}

	public UnlinkedMongoContextFactory getUnlinkedMongoContextFactory() {
		return unlinkedMongoContextFactory;
	}

	public EntityReferenceFactory getEntityReferenceFactory() {
		return entityReferenceFactory;
	}
}
