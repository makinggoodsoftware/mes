package com.mgs.mes.meta.init;

import com.mgs.mes.meta.Validator;

import java.util.List;

public class MongoInitializer {
	private final UnlinkedMongoContextRegistrer registrer;
	private final UnlinkedEntityDescriptorFactory unlinkedEntityDescriptorFactory;
	private final Validator validator;
	private final MongoContextFactory mongoContextFactory;
	private final UnlinkedMongoContextFactory unlinkedMongoContextFactory;

	public MongoInitializer(
			UnlinkedMongoContextRegistrer registrer,
			UnlinkedEntityDescriptorFactory unlinkedEntityDescriptorFactory,
			Validator validator,
			MongoContextFactory mongoContextFactory,
			UnlinkedMongoContextFactory unlinkedMongoContextFactory) {
		this.registrer = registrer;
		this.unlinkedEntityDescriptorFactory = unlinkedEntityDescriptorFactory;
		this.validator = validator;
		this.mongoContextFactory = mongoContextFactory;
		this.unlinkedMongoContextFactory = unlinkedMongoContextFactory;
	}

	public MongoContext from (List<EntityDescriptor> descriptors){
		descriptors.stream().forEach((descriptor)->{
					validator.validate(descriptor);

					UnlinkedEntity unlinkedEntity = unlinkedEntityDescriptorFactory.create(descriptor);
					registrer.register(unlinkedEntity);}
		);


		UnlinkedMongoContext unlinkedEntities = unlinkedMongoContextFactory.create (registrer);
		return mongoContextFactory.create(unlinkedEntities);
	}
}
