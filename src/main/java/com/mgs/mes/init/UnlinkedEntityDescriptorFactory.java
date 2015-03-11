package com.mgs.mes.init;

import com.mgs.mes.db.MongoDao;
import com.mgs.mes.db.MongoPersister;
import com.mgs.mes.db.MongoRetriever;
import com.mgs.mes.model.builder.EntityBuilderFactory;
import com.mgs.mes.model.builder.RelationshipBuilderFactory;
import com.mgs.mes.model.data.ModelData;
import com.mgs.mes.model.data.ModelDataBuilderFactory;
import com.mgs.mes.model.entity.*;
import com.mgs.mes.model.factory.ModelFactory;
import com.mgs.mes.model.relationships.MongoReferenceFactory;
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
	private final MongoReferenceFactory mongoReferenceFactory;

	public UnlinkedEntityDescriptorFactory(MongoDao mongoDao, FieldAccessorParser fieldAccessorParser, ModelDataBuilderFactory modelDataBuilderFactory, ModelFactory<DBObject> dbObjectModelFactory, ModelFactory<ModelData> modelDataModelFactory, MongoEntities mongoEntities, BeanNamingExpert beanNamingExpert, MongoReferenceFactory mongoReferenceFactory) {
		this.mongoDao = mongoDao;
		this.fieldAccessorParser = fieldAccessorParser;
		this.modelDataBuilderFactory = modelDataBuilderFactory;
		this.dbObjectModelFactory = dbObjectModelFactory;
		this.modelDataModelFactory = modelDataModelFactory;
		this.mongoEntities = mongoEntities;
		this.beanNamingExpert = beanNamingExpert;
		this.mongoReferenceFactory = mongoReferenceFactory;
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

	@SuppressWarnings("unchecked")
	private <A extends Entity, B extends Entity, T extends Entity, Z extends EntityBuilder<T>, T2 extends Relationship<A, B>, Z2 extends RelationshipBuilder<T2, A, B>>
	EntityBuilderFactory<T, Z> builder(Class<? extends T> typeOfModel, Class<Z> typeOfBuilder) {
		if (RelationshipBuilder.class.isAssignableFrom(typeOfBuilder)){
			Class<T2> typeOfModel1 = (Class<T2>) typeOfModel;
			Class<Z2> typeOfBuilder1 = (Class<Z2>) typeOfBuilder;
			RelationshipBuilderFactory<A, B, T2, Z2> relationshipBuilderFactory = new RelationshipBuilderFactory<>(
					modelDataBuilderFactory,
					fieldAccessorParser,
					beanNamingExpert,
					modelDataModelFactory,
					mongoReferenceFactory,
					typeOfModel1,
					typeOfBuilder1

			);
			return (EntityBuilderFactory<T, Z>) relationshipBuilderFactory;

		}else{
			return new EntityBuilderFactory<>(
					modelDataBuilderFactory,
					fieldAccessorParser,
					beanNamingExpert,
					modelDataModelFactory,
					(Class<T>) typeOfModel,
					typeOfBuilder
			);
		}
	}

	public <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>> UnlinkedEntity<T, Z, Y>
	create(EntityDescriptor<T, Z, Y> entityDescriptor) {
		MongoRetriever<T> retriever = retriever(entityDescriptor.getEntityType());
		MongoPersister<T, Z> persister = persister(entityDescriptor.getEntityType(), entityDescriptor.getBuilderType());
		EntityBuilderFactory<T, Z> builder = builder(entityDescriptor.getEntityType(), entityDescriptor.getBuilderType());
		return new UnlinkedEntity<>(retriever, persister, builder, entityDescriptor);
	}
}
