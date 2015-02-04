package com.mgs.mes.factory;

import com.mgs.mes.db.MongoDao;
import com.mgs.mes.db.MongoPersister;
import com.mgs.mes.db.MongoRetriever;
import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.model.MongoEntityBuilder;
import com.mgs.mes.model.MongoRelationships;
import com.mgs.mes.model.builder.ModelBuilderFactory;
import com.mgs.mes.model.data.ModelData;
import com.mgs.mes.model.data.ModelDataBuilderFactory;
import com.mgs.mes.model.factory.ModelFactory;
import com.mgs.mes.model.relationships.ModelRelationshipsBuilderFactory;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;

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

	private <T extends MongoEntity> MongoRetriever<T> retriever(Class<T> retrieveType) {
		return new MongoRetriever<>(mongoInternalDependencies.getMongoEntities(), mongoDao, mongoInternalDependencies.getDBOModelFactory(), retrieveType);
	}

	private <T extends MongoEntity, Z extends MongoEntityBuilder<T>> MongoPersister<T, Z> persister(Class<T> persistType, Class<Z> updaterType) {
		ModelBuilderFactory<T, Z> tzModelBuilderFactory = new ModelBuilderFactory<>(
				mongoInternalDependencies.getModelDataBuilderFactory(),
				mongoInternalDependencies.getFieldAccessorParser(),
				mongoInternalDependencies.getBeanNamingExpert(),
				mongoInternalDependencies.getModelDataModelFactory(),
				persistType,
				updaterType
		);
		return new MongoPersister<>(tzModelBuilderFactory, mongoDao, mongoInternalDependencies.getMongoEntities());
	}

	private <T extends MongoEntity, Z extends MongoEntityBuilder<T>> ModelBuilderFactory<T, Z> builder(Class<T> typeOfModel, Class<Z> typeOfBuilder) {
		ModelDataBuilderFactory modelDataBuilderFactory = mongoInternalDependencies.getModelDataBuilderFactory();
		FieldAccessorParser fieldAccessorParser = mongoInternalDependencies.getFieldAccessorParser();
		BeanNamingExpert beanNamingExpert = mongoInternalDependencies.getBeanNamingExpert();
		ModelFactory<ModelData> modelDataModelFactory = mongoInternalDependencies.getModelDataModelFactory();
		return new ModelBuilderFactory<>(modelDataBuilderFactory, fieldAccessorParser, beanNamingExpert, modelDataModelFactory, typeOfModel, typeOfBuilder);
	}

	private <T extends MongoEntity, Y extends MongoRelationships<T>> ModelRelationshipsBuilderFactory<T, Y> relationships(Class<T> typeOfModel, Class<Y> typeOfRelationships) {
		return null;
	}

	public <T extends MongoEntity, Z extends MongoEntityBuilder<T>, Y extends MongoRelationships<T>>
	MongoManager<T, Z, Y> manager(Class<T> typeOfModel, Class<Z> typeOfBuilder, Class<Y> typeOfRelationships) {
		mongoInternalDependencies.getModelValidator().validate(typeOfModel, typeOfBuilder);
		return new MongoManager<>(
				retriever(typeOfModel),
				persister(typeOfModel, typeOfBuilder),
				builder(typeOfModel, typeOfBuilder),
				relationships (typeOfModel, typeOfRelationships)
		);
	}

}
