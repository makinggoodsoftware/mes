package com.mgs.mes.simpleModel.entityB;

import com.mgs.mes.model.EntityBuilder;

public interface EntityBBuilder extends EntityBuilder<EntityB> {
	public EntityBBuilder withEntityBfield1(String value);
	public EntityBBuilder withEntityBfield2(String value);
}
