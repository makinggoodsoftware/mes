package com.mgs.mes.entity.data;

import com.mgs.mes.entity.data.transformer.EntityDataTransformer;
import com.mgs.mes.model.Entity;
import com.mgs.reflection.FieldAccessor;
import com.mongodb.DBObject;

import java.util.Map;

public class EntityDataFactory {
	private final EntityDataTransformer<DBObject> dboEntityDataTransformer;
	private final EntityDataTransformer<Map<FieldAccessor, Object>> mapEntityDataTransformer;

	public EntityDataFactory(EntityDataTransformer<DBObject> dboEntityDataTransformer, EntityDataTransformer<Map<FieldAccessor, Object>> mapEntityDataTransformer) {
		this.dboEntityDataTransformer = dboEntityDataTransformer;
		this.mapEntityDataTransformer = mapEntityDataTransformer;
	}

	public <T extends Entity> EntityData fromDbo(Class<T> type, DBObject dbObject) {
		return dboEntityDataTransformer.transform(type, dbObject);
	}

	public <T extends Entity> EntityData fromFieldAccessorMap(Class<T> type, Map<FieldAccessor, Object> fieldAccessorObjectMap) {
		return mapEntityDataTransformer.transform(type, fieldAccessorObjectMap);
	}

}
