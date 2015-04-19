package com.mgs.mes.simpleModel.entityF;

import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.simpleModel.entityB.EntityB;

import java.util.List;

public interface EntityFBuilder extends EntityBuilder<EntityF> {
	EntityFBuilder withOneToMany(List<EntityB> oneToMany);
}
