package com.mgs.config.mes.context;


import com.mgs.config.mes.meta.MetaConfig;
import com.mgs.config.mes.services.ServicesConfig;
import com.mgs.mes.context.MongoContextFactory;
import com.mgs.mes.context.unlinkedContext.UnlinkedMongoContextFactory;
import com.mgs.mes.db.MongoDao;

public class ContextConfig {
	private final MetaConfig metaConfig;
	private final ServicesConfig servicesConfig;

	public ContextConfig(MetaConfig metaConfig, ServicesConfig servicesConfig) {
		this.metaConfig = metaConfig;
		this.servicesConfig = servicesConfig;
	}

	public MongoContextFactory contextFactory(){
		return new MongoContextFactory(
				servicesConfig.mongoPersisterFactory(),
				servicesConfig.entityReferenceProviderFactory(),
				servicesConfig.entityBuilderProviderFactory()
		);
	}

	public UnlinkedMongoContextFactory unlinkedMongoContextFactory(MongoDao dao) {
		return new UnlinkedMongoContextFactory (
				dao,
				metaConfig.validator(),
				servicesConfig.entityRetrieverFactory()
		);
	}
}
