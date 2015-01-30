package com.mgs.mes.model.data;

import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.model.data.transformer.ModelDataTransformer;
import com.mgs.reflection.FieldAccessor;
import com.mongodb.DBObject;

import java.util.Map;

public class ModelDataFactory {
	private final ModelDataTransformer<DBObject> dboModelDataTransformer;
	private final ModelDataTransformer<Map<FieldAccessor, Object>> mapModelDataTransformer;

	public ModelDataFactory(ModelDataTransformer<DBObject> dboModelDataTransformer, ModelDataTransformer<Map<FieldAccessor, Object>> mapModelDataTransformer) {
		this.dboModelDataTransformer = dboModelDataTransformer;
		this.mapModelDataTransformer = mapModelDataTransformer;
	}

	public <T extends MongoEntity> ModelData fromDbo(Class<T> type, DBObject dbObject) {
		return dboModelDataTransformer.transform(type, dbObject);
	}

	public <T extends MongoEntity> ModelData fromFieldAccessorMap(Class<T> type, Map<FieldAccessor, Object> fieldAccessorObjectMap) {
		return mapModelDataTransformer.transform(type, fieldAccessorObjectMap);
	}
}
