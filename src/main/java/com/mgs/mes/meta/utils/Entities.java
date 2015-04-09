package com.mgs.mes.meta.utils;

import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;

public class Entities {

	private Class<?> extractFirstInterface(Class<?> aClass) {
		return aClass.getInterfaces()[0];
	}

	@SuppressWarnings("unchecked")
	public <T>
	Class<T> findBaseType(Class sourceClass, Class<T> typeToFind) {
		Class<?> parentFirstInterface = extractFirstInterface(sourceClass);

		if (parentFirstInterface == typeToFind) return (Class<T>)sourceClass;

		return findBaseType(parentFirstInterface, typeToFind);
	}

	public String collectionName(Class<? extends Entity> sourceClass){
		Class<? extends Entity> baseMongoEntityType = findBaseType(sourceClass, Entity.class);
		return baseMongoEntityType.getSimpleName();
	}

	public String builderCollectionName(EntityBuilder source) {
		return source.getEntityType().getSimpleName();
	}
}
