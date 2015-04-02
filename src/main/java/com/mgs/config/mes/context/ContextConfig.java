package com.mgs.config.mes.context;


import com.mgs.config.ReflectionConfig;
import com.mgs.config.mes.build.BuildConfig;
import com.mgs.config.mes.meta.MetaConfig;
import com.mgs.mes.context.MongoContextFactory;
import com.mgs.mes.context.unlinkedContext.UnlinkedEntityFactory;
import com.mgs.mes.context.unlinkedContext.UnlinkedMongoContextFactory;
import com.mgs.mes.db.MongoDao;

public class ContextConfig {
	private final MetaConfig metaConfig;
	private final BuildConfig buildConfig;
	private final ReflectionConfig reflectionsConfig;

	public ContextConfig(MetaConfig metaConfig, BuildConfig buildConfig, ReflectionConfig reflectionsConfig) {
		this.metaConfig = metaConfig;
		this.buildConfig = buildConfig;
		this.reflectionsConfig = reflectionsConfig;
	}

	public MongoContextFactory contextFactory (){
		return new MongoContextFactory(metaConfig.entities());
	}

	public UnlinkedMongoContextFactory unlinkedMongoContextFactory(MongoDao dao) {
		return new UnlinkedMongoContextFactory (unlinkedEntityFactory (dao), metaConfig.validator());
	}

	private UnlinkedEntityFactory unlinkedEntityFactory(MongoDao dao) {
		return new UnlinkedEntityFactory(
				dao,
				reflectionsConfig.fieldAccessorParser(),
				buildConfig.entityData().builderFactory(),
				buildConfig.factories().dbObjectEntity(),
				buildConfig.factories().entityDataEntity(),
				metaConfig.entities(),
				reflectionsConfig.beanNamingExpert(),
				buildConfig.factories().reference()
		);
	}
}
