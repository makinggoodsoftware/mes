package com.mgs.mes.services.core;

import com.mgs.mes.db.MongoDao;
import com.mgs.mes.meta.utils.Entities;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.services.core.builder.EntityBuilderProvider;
import org.bson.types.ObjectId;

public class EntityPersister<T extends Entity, Z extends EntityBuilder<T>> {
	private final EntityBuilderProvider<T, Z> entityBuilderProvider;
	private final MongoDao mongoDao;
	private final Entities entities;

	public EntityPersister(EntityBuilderProvider<T, Z> entityBuilderProvider, MongoDao mongoDao, Entities entities) {
		this.entityBuilderProvider = entityBuilderProvider;
		this.mongoDao = mongoDao;
		this.entities = entities;
	}

	public T touch(T toCreate) {
		boolean isNew = !toCreate.getId().isPresent();

		String collectionName = entities.collectionName(toCreate.getClass());
		ObjectId save = mongoDao.touch(collectionName, toCreate.asDbo());
		if (isNew){
			return entityBuilderProvider.update(toCreate).withId(save).create();
		}
		return toCreate;
	}

	public T create(Z toCreate) {
		String collectionName = entities.builderCollectionName(toCreate);
		ObjectId id = mongoDao.touch(collectionName, toCreate.asDbo());
		return toCreate.withId(id).create();
	}
}
