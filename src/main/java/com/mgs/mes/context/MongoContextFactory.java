package com.mgs.mes.context;

import com.mgs.mes.context.unlinkedContext.UnlinkedMongoContext;
import com.mgs.mes.db.MongoDao;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.services.core.EntityPersister;
import com.mgs.mes.services.core.EntityRetriever;
import com.mgs.mes.services.core.builder.EntityBuilderProvider;
import com.mgs.mes.services.core.reference.EntityReferenceProvider;
import com.mgs.mes.services.factory.EntityBuilderProviderFactory;
import com.mgs.mes.services.factory.EntityReferenceProviderFactory;
import com.mgs.mes.services.factory.MongoPersisterFactory;

import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class MongoContextFactory {
	private final MongoPersisterFactory mongoPersisterFactory;
	private final EntityReferenceProviderFactory entityReferenceProviderFactory;
	private final EntityBuilderProviderFactory entityBuilderProviderFactory;

	public MongoContextFactory(MongoPersisterFactory mongoPersisterFactory, EntityReferenceProviderFactory entityReferenceProviderFactory, EntityBuilderProviderFactory entityBuilderProviderFactory) {
		this.mongoPersisterFactory = mongoPersisterFactory;
		this.entityReferenceProviderFactory = entityReferenceProviderFactory;
		this.entityBuilderProviderFactory = entityBuilderProviderFactory;
	}

	public MongoContext create(UnlinkedMongoContext unlinkedEntities) {
		return new MongoContext(unlinkedEntities.getDescriptors().stream().collect(toMap(
				(Function<EntityDescriptor, EntityDescriptor>) (descriptor) -> descriptor,
				(descriptor) ->
						createMongoManager(
								unlinkedEntities.getMongoDao(),
								entityReferenceProviderFactory.create(unlinkedEntities.getRetrieverMap()),
								unlinkedEntities.getRetrieverMap().get(descriptor.getEntityType()),
								descriptor
						)
		)));
	}

	private <T extends Entity, Z extends EntityBuilder<T>>
	MongoManager<T,Z> createMongoManager(
			MongoDao mongoDao,
			EntityReferenceProvider entityReferenceProvider,
			EntityRetriever<T> entityRetriever,
			EntityDescriptor<T, Z> descriptor
	) {
		EntityPersister<T, Z> persister = mongoPersisterFactory.create(mongoDao, entityReferenceProvider, descriptor.getEntityType(), descriptor.getBuilderType());
		EntityBuilderProvider<T, Z> builder = entityBuilderProviderFactory.builder(entityReferenceProvider, descriptor.getEntityType(), descriptor.getBuilderType());
		return new MongoManager<>(
				entityRetriever,
				persister,
				builder
		);

	}

}
