package com.mgs.mes;

import com.mgs.mes.model.MongoEntity;

public interface EntityA extends MongoEntity {
	public String getEntityAfield1();
	public String getEntityAfield2();
	public EntityB getEmbedded();
}
