package com.mgs.mes.init;

import com.mgs.mes.model.entity.Entity;
import com.mgs.mes.model.entity.EntityBuilder;
import com.mgs.mes.model.entity.Relationships;

public class EntityDescriptorFactory {
	public <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>>
	EntityDescriptor<T,Z,Y> create (
			Class<T> typeOfEntity,
			Class<Z> typeOfBuilder,
			Class<Y> typeOfRelationships
	){
		return new EntityDescriptor<>(typeOfEntity, typeOfBuilder, typeOfRelationships);
	}
}
