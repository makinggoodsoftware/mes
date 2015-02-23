package com.mgs.mes.entityA;

import com.mgs.mes.entityB.EntityB;
import com.mgs.mes.model.entity.Entity;

public interface EntityA extends Entity {
	public String getEntityAfield1();
	public String getEntityAfield2();
	public EntityB getEmbedded();
}
