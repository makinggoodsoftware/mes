package com.mgs.mes.simpleModel.entityA;

import com.mgs.mes.model.Entity;
import com.mgs.mes.simpleModel.entityB.EntityB;

public interface EntityA extends Entity {
	public String getEntityAfield1();
	public String getEntityAfield2();
	public EntityB getEmbedded();
}
