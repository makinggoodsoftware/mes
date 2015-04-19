package com.mgs.mes.simpleModel.entityE;

import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.simpleModel.entityB.EntityB;

public interface EntityEBuilder extends EntityBuilder<EntityE> {
	EntityEBuilder withOneToOne (EntityB oneToOne);
}
