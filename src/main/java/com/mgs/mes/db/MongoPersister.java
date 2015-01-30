package com.mgs.mes.db;

import com.mgs.mes.model.ModelBuilder;
import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.model.builder.ModelBuilderFactory;
import com.mgs.mes.utils.MongoEntities;
import org.bson.types.ObjectId;

public class MongoPersister<T extends MongoEntity, Z extends ModelBuilder<T>> {
	private final ModelBuilderFactory<T, Z> modelBuilderFactory;
	private final MongoDao mongoDao;
	private final MongoEntities mongoEntities;

	public MongoPersister(ModelBuilderFactory<T, Z> modelBuilderFactory, MongoDao mongoDao, MongoEntities mongoEntities) {
		this.modelBuilderFactory = modelBuilderFactory;
		this.mongoDao = mongoDao;
		this.mongoEntities = mongoEntities;
	}

	public T create(T toCreate) {
		Class<? extends MongoEntity> sourceClass = toCreate.getClass();

		ObjectId save = mongoDao.save(mongoEntities.collectionName(sourceClass), toCreate.asDbo());
		return modelBuilderFactory.update(toCreate).withId(save).create();
	}

	public void update(MongoEntity toUpdate) {
		mongoDao.save(mongoEntities.collectionName(toUpdate.getClass()), toUpdate.asDbo());
	}
}
