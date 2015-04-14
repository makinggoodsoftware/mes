package com.mgs.mes.context;

import com.mgs.mes.build.data.EntityData;
import com.mgs.mes.build.data.EntityDataBuilderFactory;
import com.mgs.mes.build.factory.builder.EntityBuilderFactory;
import com.mgs.mes.build.factory.entity.EntityFactory;
import com.mgs.mes.build.factory.reference.EntityReferenceFactory;
import com.mgs.mes.context.unlinkedContext.UnlinkedMongoContext;
import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.db.MongoDao;
import com.mgs.mes.db.MongoPersister;
import com.mgs.mes.meta.utils.Entities;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mgs.reflection.Reflections;

import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class MongoContextFactory {
	private final Entities entities;
	private final EntityFactory<EntityData> modelDataEntityFactory;
	private final FieldAccessorParser fieldAccessorParser;
	private final EntityDataBuilderFactory entityDataBuilderFactory;
	private final BeanNamingExpert beanNamingExpert;
	private final Reflections reflections;

	public MongoContextFactory(Entities entities, EntityFactory<EntityData> modelDataEntityFactory, FieldAccessorParser fieldAccessorParser, EntityDataBuilderFactory entityDataBuilderFactory, BeanNamingExpert beanNamingExpert, Reflections reflections) {
		this.entities = entities;
		this.modelDataEntityFactory = modelDataEntityFactory;
		this.fieldAccessorParser = fieldAccessorParser;
		this.entityDataBuilderFactory = entityDataBuilderFactory;
		this.beanNamingExpert = beanNamingExpert;
		this.reflections = reflections;
	}

	public MongoContext create(UnlinkedMongoContext unlinkedEntities) {
		EntityReferenceFactory entityReferenceFactory = new EntityReferenceFactory(entityDataBuilderFactory, entities, unlinkedEntities.getRetrieverMap());
		Map<EntityDescriptor, MongoManager> managersByEntity = buildManagersMap(entityReferenceFactory, unlinkedEntities);
		return new MongoContext(managersByEntity);
	}

	private Map<EntityDescriptor, MongoManager> buildManagersMap(
			EntityReferenceFactory entityReferenceFactory,
			UnlinkedMongoContext unlinkedMongoContext
	) {
		return unlinkedMongoContext.getDescriptors().stream()
				.collect(toMap(
						(Function<EntityDescriptor, EntityDescriptor>) (descriptor) -> descriptor,
						(descriptor) ->
								createMongoManager(
										unlinkedMongoContext.getMongoDao(),
										entityReferenceFactory,
										unlinkedMongoContext.getRetrieverMap().get(descriptor.getEntityType()),
										descriptor
								)
				));
	}

	private <T extends Entity, Z extends EntityBuilder<T>>
	MongoPersister<T, Z> persister(MongoDao mongoDao, EntityReferenceFactory entityReferenceFactory, Class<T> persistType, Class<Z> updaterType) {
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

	private <T extends Entity, Z extends EntityBuilder<T>>
	MongoManager<T,Z> createMongoManager(
			MongoDao mongoDao,
			EntityReferenceFactory entityReferenceFactory,
			EntityRetriever<T> entityRetriever,
			EntityDescriptor<T, Z> descriptor
	) {
		MongoPersister<T, Z> persister = persister(mongoDao, entityReferenceFactory, descriptor.getEntityType(), descriptor.getBuilderType());
		EntityBuilderFactory<T, Z> builder = builder(entityReferenceFactory, descriptor.getEntityType(), descriptor.getBuilderType());
		return new MongoManager<>(
				entityRetriever,
				persister,
				builder
		);

	}

}
