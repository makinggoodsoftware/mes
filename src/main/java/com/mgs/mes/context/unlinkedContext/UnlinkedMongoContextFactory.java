package com.mgs.mes.context.unlinkedContext;

import com.mgs.mes.build.factory.core.EntityRetrieverFactory;
import com.mgs.mes.context.EntityDescriptor;
import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.db.MongoDao;
import com.mgs.mes.meta.utils.Validator;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class UnlinkedMongoContextFactory {
	private final MongoDao mongoDao;
	private final Validator validator;
	private final EntityRetrieverFactory entityRetrieverFactory;

	public UnlinkedMongoContextFactory(MongoDao mongoDao, Validator validator, EntityRetrieverFactory entityRetrieverFactory) {
		this.mongoDao = mongoDao;
		this.validator = validator;
		this.entityRetrieverFactory = entityRetrieverFactory;
	}

	public UnlinkedMongoContext createUnlinkedContext(List<EntityDescriptor> descriptors){
		UnlinkedEntitiesSet descriptorsByEntity = new UnlinkedEntitiesSet();

		descriptors.stream().forEach((descriptor)->{
				validator.validate(descriptor);

				UnlinkedEntity unlinkedEntity = create(descriptor);
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
		return new UnlinkedMongoContext(mongoDao, unlinkedEntities, retrieverMap);
	}


	private <T extends Entity, Z extends EntityBuilder<T>>
	UnlinkedEntity<T, Z> create(EntityDescriptor<T, Z> entityDescriptor) {
		EntityRetriever<T> retriever = entityRetrieverFactory.createRetriever(mongoDao, entityDescriptor);
		return new UnlinkedEntity<>(retriever, entityDescriptor);
	}
}
