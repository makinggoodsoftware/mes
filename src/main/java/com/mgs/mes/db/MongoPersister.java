package com.mgs.mes.db;

import com.mgs.mes.build.factory.builder.EntityBuilderFactory;
import com.mgs.mes.meta.utils.Entities;
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

	public T touch(T toCreate) {
		boolean isNew = !toCreate.getId().isPresent();

		String collectionName = entities.collectionName(toCreate.getClass());
		ObjectId save = mongoDao.touch(collectionName, toCreate.asDbo());
		if (isNew){
			return entityBuilderFactory.update(toCreate).withId(save).create();
		}
		return toCreate;
	}

	public T create(Z toCreate) {
		String collectionName = entities.builderCollectionName(toCreate);
		ObjectId id = mongoDao.touch(collectionName, toCreate.asDbo());
		return toCreate.withId(id).create();
	}
}
