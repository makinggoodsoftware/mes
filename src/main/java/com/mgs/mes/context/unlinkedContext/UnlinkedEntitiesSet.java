package com.mgs.mes.context.unlinkedContext;

import com.mgs.mes.context.EntityDescriptor;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;

import java.util.HashMap;
import java.util.Map;

public class UnlinkedEntitiesSet {
	private final Map<EntityDescriptor, UnlinkedEntity> descriptorsByEntity = new HashMap<>();

	public <T extends Entity, Z extends EntityBuilder<T>> void put(UnlinkedEntity<T, Z> unlinkedEntity) {
		EntityDescriptor<T, Z> entityDescriptor = unlinkedEntity.getEntityDescriptor();

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
