package com.mgs.mes.meta.init;

import com.mgs.mes.db.MongoDao;

import java.util.List;

public class MongoOrchestrator {
	private final MongoDaoFactory mongoDaoFactory;
	private final MongoInitializerFactory mongoInitializerFactory;

	public MongoOrchestrator(MongoDaoFactory mongoDaoFactory, MongoInitializerFactory mongoInitializerFactory) {
		this.mongoDaoFactory = mongoDaoFactory;
		this.mongoInitializerFactory = mongoInitializerFactory;
	}

	public MongoContext createContext(String host, int port, String dbName, List<EntityDescriptor> descriptors){
		MongoDao mongoDao = mongoDaoFactory.create(host, port, dbName);
		return mongoInitializerFactory.create(mongoDao).from(descriptors);
	}
}
