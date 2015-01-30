package com.mgs.mes.model.data.transformer;

import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.model.data.ModelData;

public interface ModelDataTransformer <T>{
	public ModelData transform (Class<? extends MongoEntity> type, T source);
}
