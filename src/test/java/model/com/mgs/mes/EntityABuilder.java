package com.mgs.mes;

import com.mgs.mes.model.MongoEntityBuilder;

public interface EntityABuilder extends MongoEntityBuilder<EntityA> {
	public EntityABuilder withEntityAfield1(String value);
	public EntityABuilder withEntityAfield2(String value);
	public EntityABuilder withEmbedded (EntityB entityB);
}
