package com.mgs.mes.entityA;

import com.mgs.mes.entityB.EntityB;
import com.mgs.mes.model.EntityBuilder;

public interface EntityABuilder extends EntityBuilder<EntityA> {
	public EntityABuilder withEntityAfield1(String value);
	public EntityABuilder withEntityAfield2(String value);
	public EntityABuilder withEmbedded (EntityB entityB);
}
