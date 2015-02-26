package com.mgs.mes.factory;

import com.mgs.mes.model.entity.Entity;
import com.mgs.mes.model.entity.EntityBuilder;
import com.mgs.mes.model.entity.Relationships;

import java.util.Map;

public class MongoContext {
	private final Map<Class<? extends Entity>, MongoManager> managersByEntity;

	public MongoContext(Map<Class<? extends Entity>, MongoManager> managersByEntity) {
		this.managersByEntity = managersByEntity;
	}

	public <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>>
	MongoManager<T, Z, Y> manager (Class<T> entity) {
		//noinspection unchecked
		return managersByEntity.get(entity);
	}
}
