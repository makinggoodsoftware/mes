package com.mgs.mes;

import com.mgs.mes.model.MongoRelationships;

public interface EntityARelationships extends MongoRelationships<EntityA> {
	EntityA_EntityCBuilder hasEntityC(EntityC c);
}
