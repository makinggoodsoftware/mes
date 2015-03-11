package com.mgs.mes.init;

import com.mgs.mes.model.builder.RelationshipBuilderFactory;
import com.mgs.mes.model.entity.Entity;
import com.mgs.mes.model.entity.EntityBuilder;
import com.mgs.mes.model.entity.RelationshipBuilder;
import com.mgs.mes.model.entity.Relationships;
import com.mgs.mes.model.relationships.RelationshipsFactory;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class MongoContextFactory {

	public MongoContextFactory() {
	}

	public MongoContext create(UnlinkedMongoContext unlinkedEntities) {
		return new MongoContext(buildManagersMap(unlinkedEntities));
	}

	private Map<EntityDescriptor, MongoManager> buildManagersMap(UnlinkedMongoContext unlinkedMongoContext) {
		//noinspection unchecked
		return unlinkedMongoContext.getUnlinkedEntities().entrySet().stream()
				.collect(toMap(
						Map.Entry::getKey,
						(entrySet) ->
								createMongoManager(
												entrySet.getValue(),
												unlinkedMongoContext.getRelationshipBuilderFactories(),
												entrySet.getKey().getRelationshipsType()
								)
				));
	}

	private <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>>
	MongoManager<T,Z,Y> createMongoManager(
			UnlinkedEntity<T,Z,Y> unlinkedEntity,
			Map<Class<? extends RelationshipBuilder>, RelationshipBuilderFactory> relationships,
			Class<Y> relationshipsType
	) {
		return new MongoManager<>(
				unlinkedEntity.getRetriever(),
				unlinkedEntity.getPersister(),
				unlinkedEntity.getBuilder(),
				new RelationshipsFactory<>(relationships, relationshipsType)
		);

	}

}
