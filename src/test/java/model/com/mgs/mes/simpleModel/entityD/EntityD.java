package com.mgs.mes.simpleModel.entityD;

import com.mgs.mes.model.Entity;
import com.mgs.mes.simpleModel.entityB.EntityB;

import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public interface EntityD extends Entity {
	public List<EntityB> getComplexList();
}
