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
import com.mgs.mes.model.relationships.MongoReferenceFactory;
import com.mgs.mes.model.relationships.RelationshipsFactory;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;
import java.util.Map;

public class MongoFactory {
	private final MongoInternalDependencies mongoInternalDependencies;
	private final MongoDao mongoDao;

	private MongoFactory(MongoInternalDependencies mongoInternalDependencies, MongoDao mongoDao) {
		this.mongoInternalDependencies = mongoInternalDependencies;
		this.mongoDao = mongoDao;
	}

	public static MongoFactory from(String host, String dbName, int port){
		try {
			MongoClient localhost = new MongoClient(host, port);
			DB db = localhost.getDB(dbName);
			MongoDao mongoDao = new MongoDao(db);
			MongoInternalDependencies dependencies = MongoInternalDependencies.init();
			return new MongoFactory(dependencies, mongoDao);
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
	}

	private <T extends Entity> MongoRetriever<T> retriever(Class<T> retrieveType) {
		return new MongoRetriever<>(mongoInternalDependencies.getMongoEntities(), mongoDao, mongoInternalDependencies.getDBOModelFactory(), retrieveType);
	}

	private <T extends Entity, Z extends EntityBuilder<T>> MongoPersister<T, Z> persister(Class<T> persistType, Class<Z> updaterType) {
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

	private <T extends Entity, Z extends EntityBuilder<T>> EntityBuilderFactory<T, Z> builder(Class<T> typeOfModel, Class<Z> typeOfBuilder) {
		ModelDataBuilderFactory modelDataBuilderFactory = mongoInternalDependencies.getModelDataBuilderFactory();
		FieldAccessorParser fieldAccessorParser = mongoInternalDependencies.getFieldAccessorParser();
		BeanNamingExpert beanNamingExpert = mongoInternalDependencies.getBeanNamingExpert();
		ModelFactory<ModelData> modelDataModelFactory = mongoInternalDependencies.getModelDataModelFactory();
		MongoReferenceFactory mongoReferenceFactory = mongoInternalDependencies.getMongoReferenceFactory();
		return new EntityBuilderFactory<>(modelDataBuilderFactory, fieldAccessorParser, beanNamingExpert, modelDataModelFactory, typeOfModel, typeOfBuilder);
	}

	private <T extends Entity, Y extends Relationships<T>> RelationshipsFactory<T, Y> relationships(
			Class<Y> typeOfRelationships,
			Map<Class<? extends RelationshipBuilder>, RelationshipBuilderFactory> modelBuildersByType
	) {
		return new RelationshipsFactory<>(typeOfRelationships, modelBuildersByType);
	}

	private Map<Class<? extends RelationshipBuilder>, RelationshipBuilderFactory> createBuildersByTpeMap() {
		return null;
	}

	public <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>>
	MongoManager<T, Z, Y> manager(Class<T> typeOfModel, Class<Z> typeOfBuilder, Class<Y> typeOfRelationships) {
		mongoInternalDependencies.getValidator().validate(typeOfModel, typeOfBuilder);
		return new MongoManager<T, Z, Y>(
				retriever(typeOfModel),
				persister(typeOfModel, typeOfBuilder),
				builder(typeOfModel, typeOfBuilder),
				relationships (typeOfRelationships, createBuildersByTpeMap())
		);
	}

}
