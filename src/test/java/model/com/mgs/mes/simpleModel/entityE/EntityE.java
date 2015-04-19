package com.mgs.mes.simpleModel.entityE;

import com.mgs.mes.model.Entity;
import com.mgs.mes.model.OneToOne;
import com.mgs.mes.simpleModel.entityB.EntityB;

public interface EntityE extends Entity{
	OneToOne<EntityB> getOneToOne ();
}
