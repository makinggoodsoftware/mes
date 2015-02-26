package com.mgs.mes.factory;

import com.mgs.mes.db.MongoDao;
import com.mgs.mes.db.MongoPersister;
import com.mgs.mes.db.MongoRetriever;
import com.mgs.mes.model.builder.EntityBuilderFactory;
import com.mgs.mes.model.data.ModelData;
import com.mgs.mes.model.data.ModelDataBuilderFactory;
import com.mgs.mes.model.entity.Entity;
import com.mgs.mes.model.entity.EntityBuilder;
import com.mgs.mes.model.entity.Relationships;
import com.mgs.mes.model.factory.ModelFactory;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;

public class UnlinkedEntityDescriptorFactory {
	private final MongoInternalDependencies mongoInternalDependencies;
	private final MongoDao mongoDao;

	public UnlinkedEntityDescriptorFactory(MongoInternalDependencies mongoInternalDependencies, MongoDao mongoDao) {
		this.mongoInternalDependencies = mongoInternalDependencies;
		this.mongoDao = mongoDao;
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
		return new EntityBuilderFactory<>(modelDataBuilderFactory, fieldAccessorParser, beanNamingExpert, modelDataModelFactory, typeOfModel, typeOfBuilder);
	}

	public <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>> UnlinkedEntityDescriptor<T, Z, Y>
	create (Class<T> entityType, Class<Z> entityBuilderType, Class<Y> relationshipsType) {
		MongoRetriever<T> retriever = retriever(entityType);
		MongoPersister<T, Z> persister = persister(entityType, entityBuilderType);
		EntityBuilderFactory<T, Z> builder = builder(entityType, entityBuilderType);
		return new UnlinkedEntityDescriptor<>(retriever, persister, builder, relationshipsType, entityBuilderType);
	}
}
