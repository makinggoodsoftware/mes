package com.mgs.mes.context.unlinkedContext;

import com.mgs.mes.context.EntityDescriptor;
import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.meta.utils.Validator;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class UnlinkedMongoContextFactory {
	private final UnlinkedEntityFactory unlinkedEntityFactory;
	private final Validator validator;

	public UnlinkedMongoContextFactory(UnlinkedEntityFactory unlinkedEntityFactory, Validator validator) {
		this.unlinkedEntityFactory = unlinkedEntityFactory;
		this.validator = validator;
	}

	public UnlinkedMongoContext createUnlinkedContext(List<EntityDescriptor> descriptors){
		UnlinkedEntitiesSet descriptorsByEntity = new UnlinkedEntitiesSet();

		descriptors.stream().forEach((descriptor)->{
				validator.validate(descriptor);

				UnlinkedEntity unlinkedEntity = unlinkedEntityFactory.create(descriptor);
				descriptorsByEntity.put(unlinkedEntity);
		});


		return from(descriptorsByEntity);
	}

	private UnlinkedMongoContext from(UnlinkedEntitiesSet descriptorsByEntity) {
		Map<EntityDescriptor, UnlinkedEntity>  unlinkedEntities = descriptorsByEntity.asMap();
		Map<Class,EntityRetriever> retrieverMap = unlinkedEntities.entrySet().stream().collect(toMap(
				(entry) -> entry.getKey().getEntityType(),
				(entry) -> entry.getValue().getRetriever()
		));
		return new UnlinkedMongoContext(unlinkedEntities, retrieverMap);
	}
}
