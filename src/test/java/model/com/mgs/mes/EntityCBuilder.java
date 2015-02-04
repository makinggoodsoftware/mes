package com.mgs.mes;

import com.mgs.mes.model.MongoEntityBuilder;

public interface EntityCBuilder extends MongoEntityBuilder<EntityC> {
	public EntityCBuilder withEntityCfield1(String value);
	public EntityCBuilder withEntityCfield2(String value);
}
