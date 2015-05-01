package com.mgs.mes.v2.entity.property.manager;

import com.mgs.mes.model.Entity;
import com.mgs.mes.model.OneToMany;
import com.mgs.mes.v2.entity.method.EntityMethodInterceptor;

public class OneToManyManager {

	public OneToManyManager() {
	}

	public EntityMethodInterceptor onRetrieveAll(OneToMany<? extends Entity> oneToMany) {
		return null;
	}
}
