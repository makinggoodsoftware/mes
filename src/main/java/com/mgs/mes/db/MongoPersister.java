package com.mgs.mes.db;

import com.mgs.mes.build.factory.builder.EntityBuilderFactory;
import com.mgs.mes.meta.Entities;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import org.bson.types.ObjectId;

public class MongoPersister<T extends Entity, Z extends EntityBuilder<T>> {
	private final EntityBuilderFactory<T, Z> entityBuilderFactory;
	private final MongoDao mongoDao;
	private final Entities entities;

	public MongoPersister(EntityBuilderFactory<T, Z> entityBuilderFactory, MongoDao mongoDao, Entities entities) {
		this.entityBuilderFactory = entityBuilderFactory;
		this.mongoDao = mongoDao;
		this.entities = entities;
	}

	public T create(T toCreate) {
		if (toCreate.getId().isPresent()) throw new IllegalStateException("Can't create a mongo entity if it already has an Id");

		String collectionName = entities.collectionName(toCreate.getClass());
		ObjectId save = mongoDao.touch(collectionName, toCreate.asDbo());
		return entityBuilderFactory.update(toCreate).withId(save).create();
	}

	public void update(Entity toUpdate) {
		if (! toUpdate.getId().isPresent()) throw new IllegalStateException("Can't update a mongo entity if it doesn't have an Id");

		String collectionName = entities.collectionName(toUpdate.getClass());
		mongoDao.touch(collectionName, toUpdate.asDbo());
	}
}
