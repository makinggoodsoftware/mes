package com.mgs.mes.init;

import com.mgs.mes.model.entity.Entity;
import com.mgs.mes.model.entity.EntityBuilder;
import com.mgs.mes.model.entity.Relationships;

import java.util.Map;

import static java.util.stream.Collectors.toList;

public class MongoContext {
	private final Map<EntityDescriptor, MongoManager> managersByEntity;

	public MongoContext(Map<EntityDescriptor, MongoManager> managersByEntity) {
		this.managersByEntity = managersByEntity;
	}

	public <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>>
	MongoManager<T, Z, Y> manager (Class<T> entityType) {
		//noinspection unchecked
		return managersByEntity.entrySet().stream().
				filter((entry)->entry.getKey().getEntityType() == entityType).
				map(Map.Entry::getValue).
				collect(toList()).
				get(0);
	}
}
