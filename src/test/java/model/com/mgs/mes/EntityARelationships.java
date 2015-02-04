package com.mgs.mes;

import com.mgs.mes.factory.MongoRelationships;

public interface EntityARelationships extends MongoRelationships<EntityA> {
	EntityA_EntityCBuilder hasEntityC(EntityC c);
}
