package com.mgs.mes.entity.data.transformer;

import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.model.Entity;

public interface EntityDataTransformer<T>{
	public EntityData transform (Class<? extends Entity> type, T source);
}
