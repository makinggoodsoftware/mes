package com.mgs.mes;

import com.mgs.mes.model.MongoEntityBuilder;

public interface EntityBBuilder extends MongoEntityBuilder<EntityB> {
	public EntityBBuilder withEntityBfield1(String value);
	public EntityBBuilder withEntityBfield2(String value);
}
