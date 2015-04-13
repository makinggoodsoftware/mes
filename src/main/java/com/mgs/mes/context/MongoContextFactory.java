package com.mgs.mes.context;

import com.mgs.mes.build.data.EntityData;
import com.mgs.mes.build.data.EntityDataBuilderFactory;
import com.mgs.mes.build.factory.builder.EntityBuilderFactory;
import com.mgs.mes.build.factory.builder.RelationshipBuilderFactory;
import com.mgs.mes.build.factory.entity.EntityFactory;
import com.mgs.mes.build.factory.reference.EntityReferenceFactory;
import com.mgs.mes.build.factory.relationship.RelationshipsFactory;
import com.mgs.mes.context.unlinkedContext.UnlinkedEntity;
import com.mgs.mes.context.unlinkedContext.UnlinkedMongoContext;
import com.mgs.mes.db.MongoDao;
import com.mgs.mes.db.MongoPersister;
import com.mgs.mes.meta.utils.Entities;
import com.mgs.mes.model.*;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mgs.reflection.Reflections;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class MongoContextFactory {
	private final Entities entities;
	private final EntityFactory<EntityData> modelDataEntityFactory;
	private final FieldAccessorParser fieldAccessorParser;
	private final EntityDataBuilderFactory entityDataBuilderFactory;
	private final BeanNamingExpert beanNamingExpert;
	private final MongoDao mongoDao;
	private final Reflections reflections;

	public MongoContextFactory(Entities entities, EntityFactory<EntityData> modelDataEntityFactory, FieldAccessorParser fieldAccessorParser, EntityDataBuilderFactory entityDataBuilderFactory, BeanNamingExpert beanNamingExpert, MongoDao mongoDao, Reflections reflections) {
		this.entities = entities;
		this.modelDataEntityFactory = modelDataEntityFactory;
		this.fieldAccessorParser = fieldAccessorParser;
		this.entityDataBuilderFactory = entityDataBuilderFactory;
		this.beanNamingExpert = beanNamingExpert;
		this.mongoDao = mongoDao;
		this.reflections = reflections;
	}

	public MongoContextReference create(UnlinkedMongoContext unlinkedEntities) {
		MongoContextReference mongoContextReference = new MongoContextReference();
		EntityReferenceFactory entityReferenceFactory = new EntityReferenceFactory(entityDataBuilderFactory, entities, unlinkedEntities.getRetrieverMap());
		Map<EntityDescriptor, MongoManager> managersByEntity = buildManagersMap(entityReferenceFactory, unlinkedEntities, mongoContextReference);
		MongoContext mongoContext = new MongoContext(managersByEntity);
		mongoContextReference.set(mongoContext);
		return mongoContextReference;
	}

	private <T extends Entity, Z extends EntityBuilder<T>>
	MongoPersister<T, Z> persister(EntityReferenceFactory entityReferenceFactory, Class<T> persistType, Class<Z> updaterType) {
		EntityBuilderFactory<T, Z> tzEntityBuilderFactory = new EntityBuilderFactory<>(
				entityDataBuilderFactory,
				fieldAccessorParser,
				beanNamingExpert,
				modelDataEntityFactory,
				persistType,
				updaterType,
				reflections,
				entityReferenceFactory);
		return new MongoPersister<>(tzEntityBuilderFactory, mongoDao, entities);
	}

	@SuppressWarnings("unchecked")
	private <A extends Entity, B extends Entity, T extends Entity, Z extends EntityBuilder<T>, T2 extends Relationship<A, B>, Z2 extends RelationshipBuilder<A, B, T2>>
	EntityBuilderFactory<T, Z> builder(EntityReferenceFactory entityReferenceFactory, Class<? extends T> typeOfModel, Class<Z> typeOfBuilder) {
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
					typeOfBuilder1,

					reflections);
			return (EntityBuilderFactory<T, Z>) relationshipBuilderFactory;

		}else{
			return new EntityBuilderFactory<>(
					entityDataBuilderFactory,
					fieldAccessorParser,
					beanNamingExpert,
					modelDataEntityFactory,
					(Class<T>) typeOfModel,
					typeOfBuilder,
					reflections,
					entityReferenceFactory);
		}
	}

	private Map<EntityDescriptor, MongoManager> buildManagersMap(
			EntityReferenceFactory entityReferenceFactory,
			UnlinkedMongoContext unlinkedMongoContext,
			MongoContextReference mongoContextReference
	) {
		//noinspection unchecked
		return unlinkedMongoContext.getUnlinkedEntities().entrySet().stream()
				.collect(toMap(
						Map.Entry::getKey,
						(entrySet) ->
								createMongoManager(
										entityReferenceFactory,
										entrySet.getValue(),
										mongoContextReference,
										entrySet.getKey().getRelationshipsType()
								)
				));
	}

	private <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>>
	MongoManager<T,Z,Y> createMongoManager(
			EntityReferenceFactory entityReferenceFactory,
			UnlinkedEntity<T, Z, Y> unlinkedEntity,
			MongoContextReference mongoContextReference,
			Class<Y> relationshipsType
	) {
		MongoPersister<T, Z> persister = persister(entityReferenceFactory, unlinkedEntity.getEntityDescriptor().getEntityType(), unlinkedEntity.getEntityDescriptor().getBuilderType());
		EntityBuilderFactory<T, Z> builder = builder(entityReferenceFactory, unlinkedEntity.getEntityDescriptor().getEntityType(), unlinkedEntity.getEntityDescriptor().getBuilderType());
		return new MongoManager<>(
				unlinkedEntity.getRetriever(),
				persister,
				builder,
				new RelationshipsFactory<>(relationshipsType, mongoContextReference, entities)
		);

	}

}
