package com.mgs.mes.context;

import com.mgs.mes.build.factory.relationships.RelationshipsFactory;
import com.mgs.mes.meta.unlinkedContext.UnlinkedEntity;
import com.mgs.mes.meta.unlinkedContext.UnlinkedMongoContext;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.model.Relationships;
import com.mgs.mes.utils.Entities;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class MongoContextFactory {
	private final Entities entities;

	public MongoContextFactory(Entities entities) {
		this.entities = entities;
	}

	public MongoContext create(UnlinkedMongoContext unlinkedEntities) {
		MongoContextReference mongoContextReference = new MongoContextReference();
		Map<EntityDescriptor, MongoManager> managersByEntity = buildManagersMap(unlinkedEntities, mongoContextReference);
		MongoContext mongoContext = new MongoContext(managersByEntity, unlinkedEntities.getRelationshipBuilderFactories());
		return mongoContext.initialise();
	}

	private Map<EntityDescriptor, MongoManager> buildManagersMap(UnlinkedMongoContext unlinkedMongoContext, MongoContextReference mongoContextReference) {
		//noinspection unchecked
		return unlinkedMongoContext.getUnlinkedEntities().entrySet().stream()
				.collect(toMap(
						Map.Entry::getKey,
						(entrySet) ->
								createMongoManager(
												entrySet.getValue(),
										mongoContextReference,
												entrySet.getKey().getRelationshipsType()
								)
				));
	}

	private <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>>
	MongoManager<T,Z,Y> createMongoManager(
			UnlinkedEntity<T,Z,Y> unlinkedEntity,
			MongoContextReference mongoContextReference,
			Class<Y> relationshipsType
	) {
		return new MongoManager<>(
				unlinkedEntity.getRetriever(),
				unlinkedEntity.getPersister(),
				unlinkedEntity.getBuilder(),
				new RelationshipsFactory<>(relationshipsType, mongoContextReference, entities)
		);

	}

}
