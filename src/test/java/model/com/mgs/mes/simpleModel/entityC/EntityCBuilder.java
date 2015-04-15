package com.mgs.mes.simpleModel.entityC;

import com.mgs.mes.model.EntityBuilder;

import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public interface EntityCBuilder extends EntityBuilder<EntityC> {
	public EntityCBuilder withList(List<String> strings);
	public EntityCBuilder withString(String string);
}
