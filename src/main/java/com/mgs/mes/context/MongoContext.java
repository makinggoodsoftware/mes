package com.mgs.mes.context;

import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;

import java.util.Map;

import static java.util.stream.Collectors.toList;

public class MongoContext {
	private final Map<EntityDescriptor, MongoManager> managersByEntity;

	public MongoContext(Map<EntityDescriptor, MongoManager> managersByEntity) {
		this.managersByEntity = managersByEntity;
	}

	public <T extends Entity, Z extends EntityBuilder<T>>
	MongoManager<T, Z> manager (EntityDescriptor<T, Z> entityDescriptor) {
		//noinspection unchecked
		return managersByEntity.get(entityDescriptor);
	}

	public <T extends Entity, Z extends EntityBuilder<T>>
	MongoManager<T, Z> manager (Class<T> entityType) {
		//noinspection unchecked
		return managersByEntity.entrySet().stream().
				filter((entry)->entry.getKey().getEntityType() == entityType).
				map(Map.Entry::getValue).
				collect(toList()).
				get(0);
	}

	@SuppressWarnings("RedundantIfStatement")
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MongoContext)) return false;

		MongoContext that = (MongoContext) o;

		if (!managersByEntity.equals(that.managersByEntity)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return managersByEntity.hashCode();
	}
}
