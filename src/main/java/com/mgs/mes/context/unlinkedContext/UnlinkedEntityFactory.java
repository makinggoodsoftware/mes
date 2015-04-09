package com.mgs.mes.context.unlinkedContext;

import com.mgs.mes.build.data.EntityData;
import com.mgs.mes.build.data.EntityDataBuilderFactory;
import com.mgs.mes.build.factory.builder.EntityBuilderFactory;
import com.mgs.mes.build.factory.builder.RelationshipBuilderFactory;
import com.mgs.mes.build.factory.entity.EntityFactory;
import com.mgs.mes.build.factory.reference.EntityReferenceFactory;
import com.mgs.mes.context.EntityDescriptor;
import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.db.MongoDao;
import com.mgs.mes.db.MongoPersister;
import com.mgs.mes.meta.utils.Entities;
import com.mgs.mes.model.*;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mongodb.DBObject;

public class UnlinkedEntityFactory {
	private final MongoDao mongoDao;
	private final FieldAccessorParser fieldAccessorParser;
	private final EntityDataBuilderFactory entityDataBuilderFactory;
	private final EntityFactory<DBObject> dbObjectEntityFactory;
	private final EntityFactory<EntityData> modelDataEntityFactory;
	private final Entities entities;
	private final BeanNamingExpert beanNamingExpert;
	private final EntityReferenceFactory entityReferenceFactory;

	public UnlinkedEntityFactory(MongoDao mongoDao, FieldAccessorParser fieldAccessorParser, EntityDataBuilderFactory entityDataBuilderFactory, EntityFactory<DBObject> dbObjectEntityFactory, EntityFactory<EntityData> modelDataEntityFactory, Entities entities, BeanNamingExpert beanNamingExpert, EntityReferenceFactory entityReferenceFactory) {
		this.mongoDao = mongoDao;
		this.fieldAccessorParser = fieldAccessorParser;
		this.entityDataBuilderFactory = entityDataBuilderFactory;
		this.dbObjectEntityFactory = dbObjectEntityFactory;
		this.modelDataEntityFactory = modelDataEntityFactory;
		this.entities = entities;
		this.beanNamingExpert = beanNamingExpert;
		this.entityReferenceFactory = entityReferenceFactory;
	}

	private <T extends Entity> EntityRetriever<T> retriever(Class<T> retrieveType) {
		return new EntityRetriever<>(entities, mongoDao, dbObjectEntityFactory, retrieveType);
	}

	private <T extends Entity, Z extends EntityBuilder<T>>
	MongoPersister<T, Z> persister(Class<T> persistType, Class<Z> updaterType) {
		EntityBuilderFactory<T, Z> tzEntityBuilderFactory = new EntityBuilderFactory<>(
				entityDataBuilderFactory,
				fieldAccessorParser,
				beanNamingExpert,
				modelDataEntityFactory,
				persistType,
				updaterType
		);
		return new MongoPersister<>(tzEntityBuilderFactory, mongoDao, entities);
	}

	@SuppressWarnings("unchecked")
	private <A extends Entity, B extends Entity, T extends Entity, Z extends EntityBuilder<T>, T2 extends Relationship<A, B>, Z2 extends RelationshipBuilder<A, B, T2>>
	EntityBuilderFactory<T, Z> builder(Class<? extends T> typeOfModel, Class<Z> typeOfBuilder) {
		if (RelationshipBuilder.class.isAssignableFrom(typeOfBuilder)){
			Class<T2> typeOfModel1 = (Class<T2>) typeOfModel;
			Class<Z2> typeOfBuilder1 = (Class<Z2>) typeOfBuilder;
			RelationshipBuilderFactory<A, B, T2, Z2> relationshipBuilderFactory = new RelationshipBuilderFactory<>(
					entityDataBuilderFactory,
					fieldAccessorParser,
					beanNamingExpert,
					modelDataEntityFactory,
					entityReferenceFactory,
					typeOfModel1,
					typeOfBuilder1

			);
			return (EntityBuilderFactory<T, Z>) relationshipBuilderFactory;

		}else{
			return new EntityBuilderFactory<>(
					entityDataBuilderFactory,
					fieldAccessorParser,
					beanNamingExpert,
					modelDataEntityFactory,
					(Class<T>) typeOfModel,
					typeOfBuilder
			);
		}
	}

	public <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>>
	UnlinkedEntity<T, Z, Y> create(EntityDescriptor<T, Z, Y> entityDescriptor) {
		EntityRetriever<T> retriever = retriever(entityDescriptor.getEntityType());
		MongoPersister<T, Z> persister = persister(entityDescriptor.getEntityType(), entityDescriptor.getBuilderType());
		EntityBuilderFactory<T, Z> builder = builder(entityDescriptor.getEntityType(), entityDescriptor.getBuilderType());
		return new UnlinkedEntity<>(retriever, persister, builder, entityDescriptor);
	}
}
