package com.mgs.mes.orm.modelData.transformer;

import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.orm.ModelData;
import com.mgs.reflection.FieldAccessor;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class FieldAccessorMapTransformer implements ModelDataTransformer<Map<FieldAccessor, Object>>{
	@Override
	public ModelData transform(Class<? extends MongoEntity> type, Map<FieldAccessor, Object> fieldValuesByAccessor) {
		return new ModelData(buildDbo(fieldValuesByAccessor), buildMethodMap(fieldValuesByAccessor));
	}

	private Map<String, Object> buildMethodMap(Map<FieldAccessor, Object> fieldsByGetterMethodName) {
		Stream<Map.Entry<FieldAccessor, Object>> stream = fieldsByGetterMethodName.entrySet().stream();
		Map<String, Object> methodMap = new HashMap<>();
		stream.forEach(fieldValueByAccessorEntry -> {
			String methodName = fieldValueByAccessorEntry.getKey().getMethodName();
			Object value = fieldValueByAccessorEntry.getValue();
			methodMap.put(methodName, value);
		});
		return methodMap;
	}

	private DBObject buildDbo(Map<FieldAccessor, Object> fieldsByGetterMethodName) {
		Stream<Map.Entry<FieldAccessor, Object>> stream = fieldsByGetterMethodName.entrySet().stream();
		Map<String, Object> dboMap = new HashMap<>();
		stream.forEach(fieldValueByAccessorEntry -> {
			String fieldName = fieldValueByAccessorEntry.getKey().getFieldName();
			Object value = fieldValueByAccessorEntry.getValue();
			if (value != null && MongoEntity.class.isAssignableFrom(value.getClass())){
				MongoEntity mongoEntity = (MongoEntity) value;
				dboMap.put(fieldName, mongoEntity.asDbo());
			} else if (fieldName.equals("id")) {
				Optional<?> optionalValue = (Optional<?>) value;
				dboMap.put("_id", optionalValue.isPresent() ? optionalValue.get() : null);
			} else {
				dboMap.put(fieldName, value);
			}
		});
		return new BasicDBObject(dboMap);
	}
}
