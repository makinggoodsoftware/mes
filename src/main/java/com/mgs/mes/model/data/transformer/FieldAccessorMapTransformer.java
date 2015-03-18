package com.mgs.mes.model.data.transformer;

import com.mgs.mes.model.data.EntityData;
import com.mgs.mes.model.entity.Entity;
import com.mgs.reflection.FieldAccessor;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class FieldAccessorMapTransformer implements EntityDataTransformer<Map<FieldAccessor, Object>> {
	@Override
	public EntityData transform(Class<? extends Entity> type, Map<FieldAccessor, Object> fieldValuesByAccessor) {
		return new EntityData(buildDbo(fieldValuesByAccessor), buildMethodMap(fieldValuesByAccessor));
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
			if (value != null && Entity.class.isAssignableFrom(value.getClass())){
				Entity entity = (Entity) value;
				dboMap.put(fieldName, entity.asDbo());
			} else if (fieldName.equals("id")) {
				Optional<?> optionalValue = (Optional<?>) value;
				if (optionalValue == null) throw new IllegalStateException("The id can never be null");
				dboMap.put("_id", optionalValue.isPresent() ? optionalValue.get() : null);
			} else {
				dboMap.put(fieldName, value);
			}
		});
		return new BasicDBObject(dboMap);
	}
}
