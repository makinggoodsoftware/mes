package com.mgs.mes.context;

import com.mgs.mes.build.data.EntityData;
import com.mgs.mes.build.data.EntityDataBuilderFactory;
import com.mgs.mes.build.factory.builder.EntityBuilderFactory;
import com.mgs.mes.build.factory.entity.EntityFactory;
import com.mgs.mes.build.factory.reference.EntityReferenceFactory;
import com.mgs.mes.context.unlinkedContext.UnlinkedEntity;
import com.mgs.mes.context.unlinkedContext.UnlinkedMongoContext;
import com.mgs.mes.db.MongoDao;
import com.mgs.mes.db.MongoPersister;
import com.mgs.mes.meta.utils.Entities;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
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
		Map<EntityDescriptor, MongoManager> managersByEntity = buildManagersMap(entityReferenceFactory, unlinkedEntities);
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
	private <T extends Entity, Z extends EntityBuilder<T>>
	EntityBuilderFactory<T, Z> builder(EntityReferenceFactory entityReferenceFactory, Class<? extends T> typeOfModel, Class<Z> typeOfBuilder) {
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

	private Map<EntityDescriptor, MongoManager> buildManagersMap(
			EntityReferenceFactory entityReferenceFactory,
			UnlinkedMongoContext unlinkedMongoContext
	) {
		//noinspection unchecked
		return unlinkedMongoContext.getUnlinkedEntities().entrySet().stream()
				.collect(toMap(
						Map.Entry::getKey,
						(entrySet) ->
								createMongoManager(
										entityReferenceFactory,
										entrySet.getValue()
								)
				));
	}

	private <T extends Entity, Z extends EntityBuilder<T>>
	MongoManager<T,Z> createMongoManager(
			EntityReferenceFactory entityReferenceFactory,
			UnlinkedEntity<T, Z> unlinkedEntity
			) {
		MongoPersister<T, Z> persister = persister(entityReferenceFactory, unlinkedEntity.getEntityDescriptor().getEntityType(), unlinkedEntity.getEntityDescriptor().getBuilderType());
		EntityBuilderFactory<T, Z> builder = builder(entityReferenceFactory, unlinkedEntity.getEntityDescriptor().getEntityType(), unlinkedEntity.getEntityDescriptor().getBuilderType());
		return new MongoManager<>(
				unlinkedEntity.getRetriever(),
				persister,
				builder
		);

	}

}
