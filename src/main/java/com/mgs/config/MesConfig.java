package com.mgs.config;

import com.mgs.config.mes.context.ContextConfig;
import com.mgs.mes.context.EntityDescriptor;
import com.mgs.mes.context.MongoContext;
import com.mgs.mes.db.MongoDao;

import java.util.List;

public class MesConfig {
	private final ContextConfig contextConfig;
	private final MongoDao mongoDao;

	public MesConfig(ContextConfig contextConfig, MongoDao mongoDao) {
		this.contextConfig = contextConfig;
		this.mongoDao = mongoDao;
	}

	public MongoContext mongoContext(List<EntityDescriptor> entityDescriptors){
		return contextConfig.contextFactory().create(
				contextConfig.unlinkedMongoContextFactory(mongoDao).createUnlinkedContext(entityDescriptors)
		);
	}
}
