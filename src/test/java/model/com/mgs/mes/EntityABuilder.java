package com.mgs.mes;

import com.mgs.mes.model.ModelBuilder;

public interface EntityABuilder extends ModelBuilder<EntityA> {
	public EntityABuilder withEntityAfield1(String value);
	public EntityABuilder withEntityAfield2(String value);
	public EntityABuilder withEmbedded (EntityB entityB);
}
