package com.mgs.mes.model.data.transformer;

import com.mgs.mes.model.data.ModelData;
import com.mgs.mes.model.entity.Entity;

public interface ModelDataTransformer <T>{
	public ModelData transform (Class<? extends Entity> type, T source);
}
