package com.mgs.mes.context.unlinkedContext;

import com.mgs.mes.build.factory.builder.RelationshipBuilderFactory;
import com.mgs.mes.context.EntityDescriptor;
import com.mgs.mes.meta.utils.Validator;
import com.mgs.mes.model.RelationshipBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UnlinkedMongoContextFactory {
	private final UnlinkedEntityFactory unlinkedEntityFactory;
	private final Validator validator;

	public UnlinkedMongoContextFactory(UnlinkedEntityFactory unlinkedEntityFactory, Validator validator) {
		this.unlinkedEntityFactory = unlinkedEntityFactory;
		this.validator = validator;
	}

	public UnlinkedMongoContext createUnlinkedContext(List<EntityDescriptor> descriptors){
		UnlinkedEntitiesSet descriptorsByEntity = new UnlinkedEntitiesSet();
		UnlinkedEntitiesSet relationshipDescriptorsByEntity = new UnlinkedEntitiesSet();

		descriptors.stream().forEach((descriptor)->{
				validator.validate(descriptor);

				UnlinkedEntity unlinkedEntity = unlinkedEntityFactory.create(descriptor);
				descriptorsByEntity.put(unlinkedEntity);

				if (unlinkedEntity.getEntityDescriptor().isRelationshipEntity()){
					relationshipDescriptorsByEntity.put(unlinkedEntity);
				}
		});


		return from(descriptorsByEntity, relationshipDescriptorsByEntity);
	}

	private UnlinkedMongoContext from(UnlinkedEntitiesSet descriptorsByEntity, UnlinkedEntitiesSet relationshipDescriptorsByEntity) {
		Map<Class<? extends RelationshipBuilder>, RelationshipBuilderFactory> relationshipBuilderFactories;

		Map<EntityDescriptor, UnlinkedEntity>  unlinkedEntities = descriptorsByEntity.asMap();
		relationshipBuilderFactories = relationshipDescriptorsByEntity.asMap().entrySet().stream().collect(Collectors.toMap(
				(entry) -> entry.getKey().getBuilderType(),
				(entry) -> (RelationshipBuilderFactory) entry.getValue().getBuilder()
		));

		return new UnlinkedMongoContext(unlinkedEntities, relationshipBuilderFactories);
	}
}
