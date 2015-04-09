package com.mgs.mes.simpleModel.entityC;

import com.mgs.mes.model.EntityBuilder;

public interface EntityCBuilder extends EntityBuilder<EntityC> {
	public EntityCBuilder withEntityCfield1(String value);
	public EntityCBuilder withEntityCfield2(String value);
}
