package com.mgs.mes.simpleModel.entityA;

import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.simpleModel.entityB.EntityB;

public interface EntityABuilder extends EntityBuilder<EntityA> {
	public EntityABuilder withEntityAfield1(String value);
	public EntityABuilder withEntityAfield2(String value);
	public EntityABuilder withEmbedded (EntityB entityB);
}
