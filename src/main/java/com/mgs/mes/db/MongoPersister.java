package com.mgs.mes.db;

import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.utils.MongoEntities;
import org.bson.types.ObjectId;

public class MongoPersister {
	private final MongoDao mongoDao;
	private final MongoEntities mongoEntities;

	public MongoPersister(MongoDao mongoDao, MongoEntities mongoEntities) {
		this.mongoDao = mongoDao;
		this.mongoEntities = mongoEntities;
	}

	public ObjectId create(MongoEntity toCreate) {
		Class<? extends MongoEntity> sourceClass = toCreate.getClass();

		return mongoDao.save(mongoEntities.collectionName(sourceClass), toCreate.asDbo());
	}

	public void update(MongoEntity toUpdate) {
		mongoDao.save(toUpdate.getClass().getName(), toUpdate.asDbo());
	}
}
