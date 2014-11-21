package com.mgs.mes;

import com.mgs.mes.model.ModelBuilder;

public interface EntityBBuilder extends ModelBuilder<EntityB> {
	public EntityBBuilder withEntityBfield1(String value);
	public EntityBBuilder withEntityBfield2(String value);
}
