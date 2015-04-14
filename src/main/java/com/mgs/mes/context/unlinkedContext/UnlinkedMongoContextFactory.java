package com.mgs.mes.context.unlinkedContext;

import com.mgs.mes.context.EntityDescriptor;
import com.mgs.mes.db.MongoDao;
import com.mgs.mes.meta.utils.Validator;
import com.mgs.mes.services.core.EntityRetriever;
import com.mgs.mes.services.factory.EntityRetrieverFactory;

import java.util.*;

public class UnlinkedMongoContextFactory {
	private final MongoDao mongoDao;
	private final Validator validator;
	private final EntityRetrieverFactory entityRetrieverFactory;

	public UnlinkedMongoContextFactory(MongoDao mongoDao, Validator validator, EntityRetrieverFactory entityRetrieverFactory) {
		this.mongoDao = mongoDao;
		this.validator = validator;
		this.entityRetrieverFactory = entityRetrieverFactory;
	}

	public UnlinkedMongoContext createUnlinkedContext(List<EntityDescriptor> descriptorsToInsert) {
		Set<EntityDescriptor> insertedDescriptors = new HashSet<>();
		Map<Class, EntityRetriever> retrieverMap = new HashMap<>();

		descriptorsToInsert.stream().forEach((descriptorToInsert) -> {
			assertDescriptorCanBeAdded(insertedDescriptors, descriptorToInsert);

			EntityRetriever retriever = entityRetrieverFactory.createRetriever(mongoDao, descriptorToInsert);
			retrieverMap.put(descriptorToInsert.getEntityType(), retriever);
			insertedDescriptors.add(descriptorToInsert);
		});


		return new UnlinkedMongoContext(mongoDao, insertedDescriptors, retrieverMap);
	}

	private void assertDescriptorCanBeAdded(Set<EntityDescriptor> descriptorsByEntity, EntityDescriptor descriptor) {
		if (descriptorsByEntity.contains(descriptor)) throw new IllegalStateException(String.format(
				"Trying to register an entity that has been already registered. Type : [%s]",
				descriptor.getEntityType()
		));

		validator.validate(descriptor);
	}


}
