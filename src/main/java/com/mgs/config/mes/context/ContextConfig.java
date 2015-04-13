package com.mgs.config.mes.context;


import com.mgs.config.mes.build.BuildConfig;
import com.mgs.config.mes.meta.MetaConfig;
import com.mgs.config.reflection.ReflectionConfig;
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

	public MongoContextFactory contextFactory (MongoDao dao){
		return new MongoContextFactory(
				metaConfig.entities(),
				buildConfig.factories().entityDataEntity(),
				reflectionsConfig.fieldAccessorParser(),
				buildConfig.entityData().builderFactory(),
				reflectionsConfig.beanNamingExpert(),
				dao,
				reflectionsConfig.reflections());
	}

	public UnlinkedMongoContextFactory unlinkedMongoContextFactory(MongoDao dao) {
		return new UnlinkedMongoContextFactory (unlinkedEntityFactory (dao), metaConfig.validator());
	}

	private UnlinkedEntityFactory unlinkedEntityFactory(MongoDao dao) {
		return new UnlinkedEntityFactory(
				dao,
				buildConfig.factories().dbObjectEntity(),
				metaConfig.entities()
		);
	}
}
