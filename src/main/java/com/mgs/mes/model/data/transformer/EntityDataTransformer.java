package com.mgs.mes.model.data.transformer;

import com.mgs.mes.model.data.EntityData;
import com.mgs.mes.model.entity.Entity;

public interface EntityDataTransformer<T>{
	public EntityData transform (Class<? extends Entity> type, T source);
}
