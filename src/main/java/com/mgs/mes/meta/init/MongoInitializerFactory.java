package com.mgs.mes.meta.init;

import com.mgs.mes.db.MongoDao;

import static com.mgs.mes.meta.init.MongoInternalDependencies.init;

public class MongoInitializerFactory {
	public MongoInitializer create (MongoDao dao) {
		MongoInternalDependencies internalDependencies = init();
		return new MongoInitializer(
				new UnlinkedMongoContextRegistrer(),
				new UnlinkedEntityDescriptorFactory(
						dao,
						internalDependencies.getFieldAccessorParser(),
						internalDependencies.getEntityDataBuilderFactory(),
						internalDependencies.getDboEntityFactory(),
						internalDependencies.getEntityDataEntityFactory(),
						internalDependencies.getEntities(),
						internalDependencies.getBeanNamingExpert(),
						internalDependencies.getEntityReferenceFactory()),
				internalDependencies.getValidator(),
				internalDependencies.getMongoContextFactory(),
				internalDependencies.getUnlinkedMongoContextFactory()
		);
	}
}
