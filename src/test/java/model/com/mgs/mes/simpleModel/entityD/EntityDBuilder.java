package com.mgs.mes.simpleModel.entityD;

import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.simpleModel.entityB.EntityB;

import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public interface EntityDBuilder extends EntityBuilder<EntityD> {
	public EntityDBuilder withComplexList(List<EntityB> embeddedList);
}
