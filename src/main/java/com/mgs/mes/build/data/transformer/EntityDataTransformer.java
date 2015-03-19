package com.mgs.mes.build.data.transformer;

import com.mgs.mes.build.data.EntityData;
import com.mgs.mes.model.Entity;

public interface EntityDataTransformer<T>{
	public EntityData transform (Class<? extends Entity> type, T source);
}
