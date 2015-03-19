package com.mgs.mes.meta.init;

import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.model.Relationships;

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
