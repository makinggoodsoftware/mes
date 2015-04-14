package com.mgs.mes.context.unlinkedContext;

import com.mgs.mes.context.EntityDescriptor;
import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.db.MongoDao;

import java.util.Map;

public class UnlinkedMongoContext {
	private final MongoDao mongoDao;
	private final Map<EntityDescriptor, UnlinkedEntity> unlinkedEntities;
	private final Map<Class, EntityRetriever> retrieverMap;

	public UnlinkedMongoContext(MongoDao mongoDao, Map<EntityDescriptor, UnlinkedEntity> unlinkedEntities, Map<Class, EntityRetriever> retrieverMap) {
		this.mongoDao = mongoDao;
		this.unlinkedEntities = unlinkedEntities;
		this.retrieverMap = retrieverMap;
	}

	public Map<EntityDescriptor, UnlinkedEntity> getUnlinkedEntities() {
		return unlinkedEntities;
	}

	public Map<Class, EntityRetriever> getRetrieverMap() {
		return retrieverMap;
	}

	public MongoDao getMongoDao() {
		return mongoDao;
	}
}
