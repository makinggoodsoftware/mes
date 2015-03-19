package com.mgs.mes.entityA;

import com.mgs.mes.entityC.EntityC;
import com.mgs.mes.model.Relationships;
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityCBuilder;

public interface EntityARelationships extends Relationships<EntityA> {
	EntityA_EntityCBuilder hasEntityC(EntityC c);
}
