package com.mgs.mes.utils;

import com.mgs.mes.model.entity.Entity;

public class Entities {

	private Class<?> extractFirstInterface(Class<?> aClass) {
		return aClass.getInterfaces()[0];
	}

	public Class<? extends Entity> findBaseMongoEntityType(Class<? extends Entity> sourceClass) {
		Class<?> parentFirstInterface = extractFirstInterface(sourceClass);

		if (parentFirstInterface == Entity.class) return sourceClass;

		//noinspection unchecked
		return findBaseMongoEntityType((Class<? extends Entity>) parentFirstInterface);
	}

	public String collectionName(Class<? extends Entity> sourceClass){
		Class<? extends Entity> baseMongoEntityType = findBaseMongoEntityType(sourceClass);
		return baseMongoEntityType.getSimpleName();
	}
}
