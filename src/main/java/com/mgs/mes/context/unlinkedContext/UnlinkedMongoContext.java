package com.mgs.mes.context.unlinkedContext;

import com.mgs.mes.context.EntityDescriptor;
import com.mgs.mes.db.MongoDao;
import com.mgs.mes.services.core.EntityRetriever;

import java.util.Map;
import java.util.Set;

public class UnlinkedMongoContext {
	private final MongoDao mongoDao;
	private final Set<EntityDescriptor> descriptors;
	private final Map<Class, EntityRetriever> retrieverMap;

	public UnlinkedMongoContext(MongoDao mongoDao, Set<EntityDescriptor> descriptors, Map<Class, EntityRetriever> retrieverMap) {
		this.mongoDao = mongoDao;
		this.descriptors = descriptors;
		this.retrieverMap = retrieverMap;
	}

	public Set<EntityDescriptor> getDescriptors() {
		return descriptors;
	}

	public Map<Class, EntityRetriever> getRetrieverMap() {
		return retrieverMap;
	}

	public MongoDao getMongoDao() {
		return mongoDao;
	}
}
