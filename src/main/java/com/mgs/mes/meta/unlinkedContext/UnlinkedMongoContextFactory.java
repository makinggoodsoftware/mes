package com.mgs.mes.meta.unlinkedContext;

import com.mgs.mes.build.factory.builder.RelationshipBuilderFactory;
import com.mgs.mes.context.EntityDescriptor;
import com.mgs.mes.model.RelationshipBuilder;

import java.util.Map;
import java.util.stream.Collectors;

public class UnlinkedMongoContextFactory {
	public UnlinkedMongoContext create(UnlinkedMongoContextRegistrer registrer) {
		Map<EntityDescriptor, UnlinkedEntity> unlinkedEntities;
		Map<Class<? extends RelationshipBuilder>, RelationshipBuilderFactory> relationshipBuilderFactories;

		unlinkedEntities = registrer.getDescriptorsByEntity().asMap();
		relationshipBuilderFactories = registrer.getRelationshipDescriptorsByEntity().asMap().entrySet().stream().collect(Collectors.toMap(
				(entry)->entry.getKey().getBuilderType(),
				(entry)->(RelationshipBuilderFactory)entry.getValue().getBuilder()
		));

		return new UnlinkedMongoContext(unlinkedEntities, relationshipBuilderFactories);
	}
}