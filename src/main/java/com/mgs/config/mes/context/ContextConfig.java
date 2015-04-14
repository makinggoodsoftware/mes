package com.mgs.config.mes.context;


import com.mgs.config.mes.build.BuildConfig;
import com.mgs.config.mes.meta.MetaConfig;
import com.mgs.config.reflection.ReflectionConfig;
import com.mgs.mes.context.MongoContextFactory;
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

	public MongoContextFactory contextFactory(){
		return new MongoContextFactory(
				metaConfig.entities(),
				buildConfig.factories().entityDataEntity(),
				reflectionsConfig.fieldAccessorParser(),
				buildConfig.entityData().builderFactory(),
				reflectionsConfig.beanNamingExpert(),
				reflectionsConfig.reflections());
	}

	public UnlinkedMongoContextFactory unlinkedMongoContextFactory(MongoDao dao) {
		return new UnlinkedMongoContextFactory (
				dao,
				metaConfig.validator(),
				buildConfig.factories().entityRetriever()
		);
	}
}
