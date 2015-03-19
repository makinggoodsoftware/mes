package com.mgs.mes.meta.init;

import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.model.Relationships;

import java.util.HashMap;
import java.util.Map;

public class UnlinkedEntitiesSet {
	private final Map<EntityDescriptor, UnlinkedEntity> descriptorsByEntity = new HashMap<>();

	public <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>> void put(UnlinkedEntity<T, Z, Y> unlinkedEntity) {
		EntityDescriptor<T, Z, Y> entityDescriptor = unlinkedEntity.getEntityDescriptor();

		if (descriptorsByEntity.get(entityDescriptor) != null) throw new IllegalStateException(String.format(
				"Trying to register an entity that has been already registered. Type : [%s]",
				entityDescriptor.getEntityType()
		));

		descriptorsByEntity.put(entityDescriptor, unlinkedEntity);
	}

	public Map<EntityDescriptor, UnlinkedEntity> asMap() {
		return descriptorsByEntity;
	}
}
