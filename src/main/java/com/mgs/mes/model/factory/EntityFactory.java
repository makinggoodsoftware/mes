package com.mgs.mes.model.factory;

import com.mgs.mes.model.entity.Entity;

public interface EntityFactory<X>{
	<T extends Entity> T from (Class<T> type, X from);
}
