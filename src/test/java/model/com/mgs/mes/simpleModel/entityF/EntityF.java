package com.mgs.mes.simpleModel.entityF;

import com.mgs.mes.model.Entity;
import com.mgs.mes.model.OneToMany;
import com.mgs.mes.simpleModel.entityB.EntityB;

public interface EntityF extends Entity{
	OneToMany<EntityB> getOneToMany();
}
