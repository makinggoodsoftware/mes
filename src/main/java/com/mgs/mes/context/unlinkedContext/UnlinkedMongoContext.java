package com.mgs.mes.context.unlinkedContext;

import com.mgs.mes.build.factory.builder.RelationshipBuilderFactory;
import com.mgs.mes.context.EntityDescriptor;
import com.mgs.mes.model.RelationshipBuilder;

import java.util.Map;

public class UnlinkedMongoContext {
	private final Map<EntityDescriptor, UnlinkedEntity> unlinkedEntities;
	private final Map<Class<? extends RelationshipBuilder>, RelationshipBuilderFactory> relationshipBuilderFactories;

	public UnlinkedMongoContext(Map<EntityDescriptor, UnlinkedEntity> unlinkedEntities, Map<Class<? extends RelationshipBuilder>, RelationshipBuilderFactory> relationshipBuilderFactories) {
		this.unlinkedEntities = unlinkedEntities;
		this.relationshipBuilderFactories = relationshipBuilderFactories;
	}

	public Map<Class<? extends RelationshipBuilder>, RelationshipBuilderFactory> getRelationshipBuilderFactories() {
		return relationshipBuilderFactories;
	}

	public Map<EntityDescriptor, UnlinkedEntity> getUnlinkedEntities() {
		return unlinkedEntities;
	}
}
