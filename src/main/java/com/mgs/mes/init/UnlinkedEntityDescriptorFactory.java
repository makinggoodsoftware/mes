package com.mgs.mes.init;

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
import com.mgs.mes.utils.MongoEntities;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mongodb.DBObject;

public class UnlinkedEntityDescriptorFactory {
	private final MongoDao mongoDao;
	private final FieldAccessorParser fieldAccessorParser;
	private final ModelDataBuilderFactory modelDataBuilderFactory;
	private final ModelFactory<DBObject> dbObjectModelFactory;
	private final ModelFactory<ModelData> modelDataModelFactory;
	private final MongoEntities mongoEntities;
	private final BeanNamingExpert beanNamingExpert;

	public UnlinkedEntityDescriptorFactory(MongoDao mongoDao, FieldAccessorParser fieldAccessorParser, ModelDataBuilderFactory modelDataBuilderFactory, ModelFactory<DBObject> dbObjectModelFactory, ModelFactory<ModelData> modelDataModelFactory, MongoEntities mongoEntities, BeanNamingExpert beanNamingExpert) {
		this.mongoDao = mongoDao;
		this.fieldAccessorParser = fieldAccessorParser;
		this.modelDataBuilderFactory = modelDataBuilderFactory;
		this.dbObjectModelFactory = dbObjectModelFactory;
		this.modelDataModelFactory = modelDataModelFactory;
		this.mongoEntities = mongoEntities;
		this.beanNamingExpert = beanNamingExpert;
	}

	private <T extends Entity> MongoRetriever<T> retriever(Class<T> retrieveType) {
		return new MongoRetriever<>(mongoEntities, mongoDao, dbObjectModelFactory, retrieveType);
	}

	private <T extends Entity, Z extends EntityBuilder<T>> MongoPersister<T, Z> persister(Class<T> persistType, Class<Z> updaterType) {
		EntityBuilderFactory<T, Z> tzEntityBuilderFactory = new EntityBuilderFactory<>(
				modelDataBuilderFactory,
				fieldAccessorParser,
				beanNamingExpert,
				modelDataModelFactory,
				persistType,
				updaterType
		);
		return new MongoPersister<>(tzEntityBuilderFactory, mongoDao, mongoEntities);
	}

	private <T extends Entity, Z extends EntityBuilder<T>> EntityBuilderFactory<T, Z> builder(Class<T> typeOfModel, Class<Z> typeOfBuilder) {
		return new EntityBuilderFactory<>(modelDataBuilderFactory, fieldAccessorParser, beanNamingExpert, modelDataModelFactory, typeOfModel, typeOfBuilder);
	}

	public <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>> UnlinkedEntity<T, Z, Y>
	create(EntityDescriptor<T, Z, Y> entityDescriptor) {
		MongoRetriever<T> retriever = retriever(entityDescriptor.getEntityType());
		MongoPersister<T, Z> persister = persister(entityDescriptor.getEntityType(), entityDescriptor.getBuilderType());
		EntityBuilderFactory<T, Z> builder = builder(entityDescriptor.getEntityType(), entityDescriptor.getBuilderType());
		return new UnlinkedEntity<>(retriever, persister, builder, entityDescriptor);
	}
}
