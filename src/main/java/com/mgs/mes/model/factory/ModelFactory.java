package com.mgs.mes.model.factory;

import com.mgs.mes.model.MongoEntity;

public interface ModelFactory <X>{
	<T extends MongoEntity> T from (Class<T> type, X from);
}
