package com.mgs.mes.context.unlinkedContext;

import com.mgs.mes.build.factory.core.EntityRetrieverFactory;
import com.mgs.mes.context.EntityDescriptor;
import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.db.MongoDao;
import com.mgs.mes.meta.utils.Validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnlinkedMongoContextFactory {
	private final MongoDao mongoDao;
	private final Validator validator;
	private final EntityRetrieverFactory entityRetrieverFactory;

	public UnlinkedMongoContextFactory(MongoDao mongoDao, Validator validator, EntityRetrieverFactory entityRetrieverFactory) {
		this.mongoDao = mongoDao;
		this.validator = validator;
		this.entityRetrieverFactory = entityRetrieverFactory;
	}

	public UnlinkedMongoContext createUnlinkedContext(List<EntityDescriptor> descriptors) {
		Map<EntityDescriptor, UnlinkedEntity> descriptorsByEntity = new HashMap<>();
		Map<Class, EntityRetriever> retrieverMap = new HashMap<>();

		descriptors.stream().forEach((descriptor) -> {
			assertDescriptorCanBeAdded(descriptorsByEntity, descriptor);


			EntityRetriever retriever = entityRetrieverFactory.createRetriever(mongoDao, descriptor);
			//noinspection unchecked
			UnlinkedEntity unlinkedEntity = new UnlinkedEntity(descriptor);

			descriptorsByEntity.put(descriptor, unlinkedEntity);
			retrieverMap.put(descriptor.getEntityType(), retriever);
		});


		return new UnlinkedMongoContext(mongoDao, descriptorsByEntity, retrieverMap);
	}

	private void assertDescriptorCanBeAdded(Map<EntityDescriptor, UnlinkedEntity> descriptorsByEntity, EntityDescriptor descriptor) {
		if (descriptorsByEntity.get(descriptor) != null) throw new IllegalStateException(String.format(
				"Trying to register an entity that has been already registered. Type : [%s]",
				descriptor.getEntityType()
		));

		validator.validate(descriptor);
	}


}
