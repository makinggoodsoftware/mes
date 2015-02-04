package com.mgs.mes;

import com.mgs.mes.model.MongoEntityBuilder;

public interface EntityA_EntityCBuilder extends MongoEntityBuilder<EntityA_EntityC> {
	EntityA_EntityCBuilder withEntityA(EntityA entityA);
	EntityA_EntityCBuilder withEntityC(EntityC entityC);
}
