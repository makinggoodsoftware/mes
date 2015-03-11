package com.mgs.mes.init;

import com.mgs.mes.db.MongoDao;

import static com.mgs.mes.init.MongoInternalDependencies.init;

public class MongoInitializerFactory {
	public MongoInitializer create (MongoDao dao) {
		MongoInternalDependencies internalDependencies = init();
		return new MongoInitializer(
				new UnlinkedMongoContextRegistrer(),
				new UnlinkedEntityDescriptorFactory(
						dao,
						internalDependencies.getFieldAccessorParser(),
						internalDependencies.getModelDataBuilderFactory(),
						internalDependencies.getDBOModelFactory(),
						internalDependencies.getModelDataModelFactory(),
						internalDependencies.getMongoEntities(),
						internalDependencies.getBeanNamingExpert()
				),
				internalDependencies.getValidator(),
				internalDependencies.getMongoContextFactory(),
				internalDependencies.getUnlinkedMongoContextFactory()
		);
	}
}
