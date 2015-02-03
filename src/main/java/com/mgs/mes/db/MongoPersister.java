package com.mgs.mes.db;

import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.model.MongoEntityBuilder;
import com.mgs.mes.model.builder.ModelBuilderFactory;
import com.mgs.mes.utils.MongoEntities;
import org.bson.types.ObjectId;

public class MongoPersister<T extends MongoEntity, Z extends MongoEntityBuilder<T>> {
	private final ModelBuilderFactory<T, Z> modelBuilderFactory;
	private final MongoDao mongoDao;
	private final MongoEntities mongoEntities;

	public MongoPersister(ModelBuilderFactory<T, Z> modelBuilderFactory, MongoDao mongoDao, MongoEntities mongoEntities) {
		this.modelBuilderFactory = modelBuilderFactory;
		this.mongoDao = mongoDao;
		this.mongoEntities = mongoEntities;
	}

	public T create(T toCreate) {
		if (toCreate.getId().isPresent()) throw new IllegalStateException("Can't create a mongo entity if it already has an Id");

		String collectionName = mongoEntities.collectionName(toCreate.getClass());
		ObjectId save = mongoDao.touch(collectionName, toCreate.asDbo());
		return modelBuilderFactory.update(toCreate).withId(save).create();
	}

	public void update(MongoEntity toUpdate) {
		if (! toUpdate.getId().isPresent()) throw new IllegalStateException("Can't update a mongo entity if it doesn't have an Id");

		String collectionName = mongoEntities.collectionName(toUpdate.getClass());
		mongoDao.touch(collectionName, toUpdate.asDbo());
	}
}
