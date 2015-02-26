package com.mgs.mes.factory;

import com.mgs.mes.db.MongoDao;
import com.mgs.mes.db.MongoPersister;
import com.mgs.mes.db.MongoRetriever;
import com.mgs.mes.model.builder.EntityBuilderFactory;
import com.mgs.mes.model.builder.RelationshipBuilderFactory;
import com.mgs.mes.model.data.ModelData;
import com.mgs.mes.model.data.ModelDataBuilderFactory;
import com.mgs.mes.model.entity.Entity;
import com.mgs.mes.model.entity.EntityBuilder;
import com.mgs.mes.model.entity.RelationshipBuilder;
import com.mgs.mes.model.entity.Relationships;
import com.mgs.mes.model.factory.ModelFactory;
import com.mgs.mes.model.relationships.RelationshipsFactory;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;

import java.util.Map;

public class MongoFactory {
	private final MongoInternalDependencies mongoInternalDependencies;
	private final MongoDao mongoDao;

	public MongoFactory(MongoInternalDependencies mongoInternalDependencies, MongoDao mongoDao) {
		this.mongoInternalDependencies = mongoInternalDependencies;
		this.mongoDao = mongoDao;
	}

	public <T extends Entity> MongoRetriever<T> retriever(Class<T> retrieveType) {
		return new MongoRetriever<>(mongoInternalDependencies.getMongoEntities(), mongoDao, mongoInternalDependencies.getDBOModelFactory(), retrieveType);
	}

	public <T extends Entity, Z extends EntityBuilder<T>> MongoPersister<T, Z> persister(Class<T> persistType, Class<Z> updaterType) {
		EntityBuilderFactory<T, Z> tzEntityBuilderFactory = new EntityBuilderFactory<>(
				mongoInternalDependencies.getModelDataBuilderFactory(),
				mongoInternalDependencies.getFieldAccessorParser(),
				mongoInternalDependencies.getBeanNamingExpert(),
				mongoInternalDependencies.getModelDataModelFactory(),
				persistType,
				updaterType
		);
		return new MongoPersister<>(tzEntityBuilderFactory, mongoDao, mongoInternalDependencies.getMongoEntities());
	}

	public <T extends Entity, Z extends EntityBuilder<T>> EntityBuilderFactory<T, Z> builder(Class<T> typeOfModel, Class<Z> typeOfBuilder) {
		ModelDataBuilderFactory modelDataBuilderFactory = mongoInternalDependencies.getModelDataBuilderFactory();
		FieldAccessorParser fieldAccessorParser = mongoInternalDependencies.getFieldAccessorParser();
		BeanNamingExpert beanNamingExpert = mongoInternalDependencies.getBeanNamingExpert();
		ModelFactory<ModelData> modelDataModelFactory = mongoInternalDependencies.getModelDataModelFactory();
		return new EntityBuilderFactory<>(modelDataBuilderFactory, fieldAccessorParser, beanNamingExpert, modelDataModelFactory, typeOfModel, typeOfBuilder);
	}

	public <T extends Entity, Y extends Relationships<T>> RelationshipsFactory<T, Y> relationships(
			Class<Y> typeOfRelationships,
			Map<Class<? extends RelationshipBuilder>, RelationshipBuilderFactory> modelBuildersByType
	) {
		return new RelationshipsFactory<>(typeOfRelationships, modelBuildersByType);
	}

}
