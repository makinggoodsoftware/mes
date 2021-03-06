package com.mgs.mes.entity.factory.entity;

import com.mgs.mes.model.Entity;

public interface EntityFactory<X>{
	<T extends Entity> T from (Class<T> type, X from);
}
