package com.mgs.mes.utils;

import com.mgs.mes.model.MongoEntity;

public class MongoEntities {

	private Class<?> extractFirstInterface(Class<?> aClass) {
		return aClass.getInterfaces()[0];
	}

	Class<? extends MongoEntity> findBaseMongoEntityType(Class<? extends MongoEntity> sourceClass) {
		Class<?> parentFirstInterface = extractFirstInterface(sourceClass);

		if (parentFirstInterface == MongoEntity.class) return sourceClass;

		//noinspection unchecked
		return findBaseMongoEntityType((Class<? extends MongoEntity>) parentFirstInterface);
	}

	public String collectionName(Class<? extends MongoEntity> sourceClass){
		Class<? extends MongoEntity> baseMongoEntityType = findBaseMongoEntityType(sourceClass);
		return baseMongoEntityType.getSimpleName();
	}
}
