package com.mgs.mes.meta.init;

import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.model.Relationships;

public class UnlinkedMongoContextRegistrer {
	private final UnlinkedEntitiesSet descriptorsByEntity = new UnlinkedEntitiesSet();
	private final UnlinkedEntitiesSet relationshipDescriptorsByEntity = new UnlinkedEntitiesSet();


	public <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>>
	void register(UnlinkedEntity<T, Z, Y> unlinkedEntity) {
		descriptorsByEntity.put(unlinkedEntity);

		if (unlinkedEntity.getEntityDescriptor().isRelationshipEntity()){
			relationshipDescriptorsByEntity.put(unlinkedEntity);
		}
	}

	public UnlinkedEntitiesSet getDescriptorsByEntity() {
		return descriptorsByEntity;
	}

	public UnlinkedEntitiesSet getRelationshipDescriptorsByEntity() {
		return relationshipDescriptorsByEntity;
	}
}