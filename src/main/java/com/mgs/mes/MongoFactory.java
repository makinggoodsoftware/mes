package com.mgs.mes;

import com.mgs.mes.db.MongoDao;
import com.mgs.mes.db.MongoPersister;
import com.mgs.mes.db.MongoRetriever;
import com.mgs.mes.model.ModelBuilder;
import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.orm.modelBuilder.ModelBuilderFactory;
import com.mgs.mes.orm.modelData.ModelDataBuilderFactory;
import com.mgs.mes.orm.modelFactory.DynamicModelFactory;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;

import static com.mgs.mes.MongoDependencies.init;

public class MongoFactory {
	private final MongoDependencies mongoDependencies;
	private final MongoDao mongoDao;

	private MongoFactory(MongoDependencies mongoDependencies, MongoDao mongoDao) {
		this.mongoDependencies = mongoDependencies;
		this.mongoDao = mongoDao;
	}

	public static MongoFactory from(String host, String dbName, int port){
		try {
			MongoClient localhost = new MongoClient(host, port);
			DB db = localhost.getDB(dbName);
			MongoDao mongoDao = new MongoDao(db);
			MongoDependencies dependencies = init();
			return new MongoFactory(dependencies, mongoDao);
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
	}

	public <T extends MongoEntity> MongoRetriever<T> retriever(Class<T> retrieveType) {
		return new MongoRetriever<>(mongoDependencies.getMongoEntities(), mongoDao, mongoDependencies.getModelFactory(), retrieveType);
	}

	public <T extends MongoEntity, Z extends ModelBuilder<T>> MongoPersister<T, Z> persister(Class<T> persistType, Class<Z> updaterType) {
		ModelBuilderFactory<T, Z> tzModelBuilderFactory = new ModelBuilderFactory<>(
				mongoDependencies.getModelDataBuilderFactory(),
				mongoDependencies.getFieldAccessorParser(),
				mongoDependencies.getBeanNamingExpert(),
				mongoDependencies.getDynamicModelFactory(),
				persistType,
				updaterType
		);
		return new MongoPersister<>(tzModelBuilderFactory, mongoDao, mongoDependencies.getMongoEntities());
	}

	public <T extends MongoEntity, Z extends ModelBuilder<T>> ModelBuilderFactory<T, Z> builder(Class<T> typeOfModel, Class<Z> typeOfBuilder) {
		mongoDependencies.getModelValidator().validate(typeOfModel, typeOfBuilder);
		ModelDataBuilderFactory modelDataBuilderFactory = mongoDependencies.getModelDataBuilderFactory();
		FieldAccessorParser fieldAccessorParser = mongoDependencies.getFieldAccessorParser();
		BeanNamingExpert beanNamingExpert = mongoDependencies.getBeanNamingExpert();
		DynamicModelFactory dynamicModelFactory = mongoDependencies.getDynamicModelFactory();
		return new ModelBuilderFactory<>(modelDataBuilderFactory, fieldAccessorParser, beanNamingExpert, dynamicModelFactory, typeOfModel, typeOfBuilder);
	}

}
