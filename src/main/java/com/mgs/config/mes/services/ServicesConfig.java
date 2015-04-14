package com.mgs.config.mes.services;

import com.mgs.config.mes.entity.EntityConfig;
import com.mgs.config.mes.meta.MetaConfig;
import com.mgs.config.reflection.ReflectionConfig;
import com.mgs.mes.services.factory.EntityBuilderProviderFactory;
import com.mgs.mes.services.factory.EntityReferenceProviderFactory;
import com.mgs.mes.services.factory.EntityRetrieverFactory;
import com.mgs.mes.services.factory.MongoPersisterFactory;

public class ServicesConfig {
	private final MetaConfig metaConfig;
	private final EntityConfig entityConfig;
	private final ReflectionConfig reflectionConfig;

	public ServicesConfig(MetaConfig metaConfig, EntityConfig entityConfig, ReflectionConfig reflectionConfig) {
		this.metaConfig = metaConfig;
		this.entityConfig = entityConfig;
		this.reflectionConfig = reflectionConfig;
	}

	public MongoPersisterFactory mongoPersisterFactory() {
		return new MongoPersisterFactory(
				metaConfig.entities(),
				entityConfig.factories().entityDataEntity(),
				reflectionConfig.fieldAccessorParser(),
				entityConfig.entityData().builderFactory(),
				reflectionConfig.beanNamingExpert(),
				reflectionConfig.reflections()
		);
	}

	public EntityReferenceProviderFactory entityReferenceProviderFactory() {
		return new EntityReferenceProviderFactory(
				entityConfig.entityData().builderFactory(),
				metaConfig.entities()
		);
	}

	public EntityBuilderProviderFactory entityBuilderProviderFactory() {
		return new EntityBuilderProviderFactory(
				entityConfig.factories().entityDataEntity(),
				reflectionConfig.fieldAccessorParser(),
				entityConfig.entityData().builderFactory(),
				reflectionConfig.beanNamingExpert(),
				reflectionConfig.reflections()
		);
	}

	public EntityRetrieverFactory entityRetrieverFactory(){
		return new EntityRetrieverFactory(
				entityConfig.factories().dbObjectEntity(),
				metaConfig.entities()
		);
	}
}
