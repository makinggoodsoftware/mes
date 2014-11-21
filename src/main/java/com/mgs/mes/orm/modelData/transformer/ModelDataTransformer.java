package com.mgs.mes.orm.modelData.transformer;

import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.orm.ModelData;

public interface ModelDataTransformer <T>{
	public ModelData transform (Class<? extends MongoEntity> type, T source);
}
