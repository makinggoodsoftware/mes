package com.mgs.mes.simpleModel.entityA;

import com.mgs.mes.model.Relationships;
import com.mgs.mes.simpleModel.entityC.EntityC;
import com.mgs.mes.simpleModel.relationships.entityA_EntityC.EntityA_EntityCBuilder;

public interface EntityARelationships extends Relationships<EntityA> {
	EntityA_EntityCBuilder hasEntityC(EntityC c);
}
