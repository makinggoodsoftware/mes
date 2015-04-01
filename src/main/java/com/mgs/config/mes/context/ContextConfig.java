package com.mgs.config.mes.context;


import com.mgs.config.mes.meta.MetaConfig;
import com.mgs.mes.context.MongoContextFactory;

public class ContextConfig {
	private final MetaConfig metaConfig;

	public ContextConfig(MetaConfig metaConfig) {
		this.metaConfig = metaConfig;
	}

	public MongoContextFactory contextFactory (){
		return new MongoContextFactory(metaConfig.entities());
	}
}
