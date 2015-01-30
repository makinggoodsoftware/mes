package com.mgs.mes;

import com.mgs.mes.db.MongoDao;
import com.mgs.mes.db.MongoPersister;
import com.mgs.mes.db.MongoRetriever;
import com.mgs.mes.model.ModelBuilder;
import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.model.builder.ModelBuilderFactory;
import com.mgs.mes.model.data.ModelDataBuilderFactory;
import com.mgs.mes.model.factory.DynamicModelFactory;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;

import static com.mgs.mes.MongoInternalDependencies.init;

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
			MongoInternalDependencies dependencies = init();
			return new MongoFactory(dependencies, mongoDao);
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
	}

	public <T extends MongoEntity> MongoRetriever<T> retriever(Class<T> retrieveType) {
		return new MongoRetriever<>(mongoInternalDependencies.getMongoEntities(), mongoDao, mongoInternalDependencies.getModelFactory(), retrieveType);
	}

	public <T extends MongoEntity, Z extends ModelBuilder<T>> MongoPersister<T, Z> persister(Class<T> persistType, Class<Z> updaterType) {
		ModelBuilderFactory<T, Z> tzModelBuilderFactory = new ModelBuilderFactory<>(
				mongoInternalDependencies.getModelDataBuilderFactory(),
				mongoInternalDependencies.getFieldAccessorParser(),
				mongoInternalDependencies.getBeanNamingExpert(),
				mongoInternalDependencies.getDynamicModelFactory(),
				persistType,
				updaterType
		);
		return new MongoPersister<>(tzModelBuilderFactory, mongoDao, mongoInternalDependencies.getMongoEntities());
	}

	public <T extends MongoEntity, Z extends ModelBuilder<T>> ModelBuilderFactory<T, Z> builder(Class<T> typeOfModel, Class<Z> typeOfBuilder) {
		mongoInternalDependencies.getModelValidator().validate(typeOfModel, typeOfBuilder);
		ModelDataBuilderFactory modelDataBuilderFactory = mongoInternalDependencies.getModelDataBuilderFactory();
		FieldAccessorParser fieldAccessorParser = mongoInternalDependencies.getFieldAccessorParser();
		BeanNamingExpert beanNamingExpert = mongoInternalDependencies.getBeanNamingExpert();
		DynamicModelFactory dynamicModelFactory = mongoInternalDependencies.getDynamicModelFactory();
		return new ModelBuilderFactory<>(modelDataBuilderFactory, fieldAccessorParser, beanNamingExpert, dynamicModelFactory, typeOfModel, typeOfBuilder);
	}

}
