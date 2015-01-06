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
	private final MongoPersister mongoPersister;

	private MongoFactory(MongoDependencies mongoDependencies, MongoDao mongoDao, MongoPersister mongoPersister) {
		this.mongoDependencies = mongoDependencies;
		this.mongoDao = mongoDao;
		this.mongoPersister = mongoPersister;
	}

	public static MongoFactory from(String host, String dbName, int port){
		try {
			MongoClient localhost = new MongoClient(host, port);
			DB db = localhost.getDB(dbName);
			MongoDao mongoDao = new MongoDao(db);
			MongoDependencies dependencies = init();
			MongoPersister mongoPersister = new MongoPersister(mongoDao, dependencies.getMongoEntities());
			return new MongoFactory(dependencies, mongoDao, mongoPersister);
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
	}

	public <T extends MongoEntity> MongoRetriever<T> retriever(Class<T> retrieveType) {
		return new MongoRetriever<>(mongoDependencies.getMongoEntities(), mongoDao, mongoDependencies.getModelFactory(), retrieveType);
	}

	public <T extends MongoEntity, Z extends ModelBuilder<T>> ModelBuilderFactory<T, Z> builder(Class<T> typeOfModel, Class<Z> typeOfBuilder) {
		mongoDependencies.getModelValidator().validate(typeOfModel, typeOfBuilder);
		ModelDataBuilderFactory modelDataBuilderFactory = mongoDependencies.getModelDataBuilderFactory();
		FieldAccessorParser fieldAccessorParser = mongoDependencies.getFieldAccessorParser();
		BeanNamingExpert beanNamingExpert = mongoDependencies.getBeanNamingExpert();
		DynamicModelFactory dynamicModelFactory = mongoDependencies.getDynamicModelFactory();
		return new ModelBuilderFactory<>(modelDataBuilderFactory, fieldAccessorParser, beanNamingExpert, typeOfModel, typeOfBuilder, dynamicModelFactory);
	}

	public MongoPersister getPersister() {
		return mongoPersister;
	}
}
