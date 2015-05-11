package com.mgs.mes.entity.data.transformer;

import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.OneToMany;
import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.ParsedType;
import com.mgs.reflection.Reflections;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class FieldAccessorMapTransformer implements EntityDataTransformer<Map<FieldAccessor, Object>> {
	private final Reflections reflections;

	public FieldAccessorMapTransformer(Reflections reflections) {
		this.reflections = reflections;
	}

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
			FieldAccessor fieldAccessor = fieldValueByAccessorEntry.getKey();
			String fieldName = fieldAccessor.getFieldName();
			Object value = fieldValueByAccessorEntry.getValue();
			if (isWrappedListOfValues(fieldAccessor)){
				List<? extends Entity> child;
				if (reflections.isAssignableTo(fieldAccessor.getDeclaredType(), OneToMany.class)){
					//noinspection unchecked
					child = ((OneToMany) value).getList();
				} else {
					//noinspection unchecked
					child = (List<? extends Entity>) value;
				}
				dboMap.put(fieldName, child.stream().map(Entity::asDbo).collect(toList()));
			}else if(isWrappedValue(value)){
				Entity casted = (Entity) value;
				dboMap.put(fieldName, casted.asDbo());
			} else if (fieldName.equals("id")) {
				Optional<?> optionalValue = (Optional<?>) value;
				dboMap.put("_id", optionalValue.isPresent() ? optionalValue.get() : null);
			} else {
				dboMap.put(fieldName, value);
			}
		});
		return new BasicDBObject(dboMap);
	}

	private boolean isWrappedListOfValues(FieldAccessor fieldAccessor) {
		if (
				!reflections.isAssignableTo(fieldAccessor.getDeclaredType(), Collection.class) &&
				!reflections.isAssignableTo(fieldAccessor.getDeclaredType(), OneToMany.class)
		) return false;
		//noinspection SimplifiableIfStatement
		if (fieldAccessor.getParsedTypes().size() == 0) return false;
		ParsedType parsedType = fieldAccessor.getParsedTypes().get(0);
//		return !parsedType.getSpecificClass().isPresent() ||
//				reflections.isAssignableTo(parsedType.getSpecificClass().get(), Entity.class);
		return false;
	}

	private boolean isWrappedValue(Object value) {
		//noinspection SimplifiableIfStatement
		if (value == null) return false;
		return reflections.isAssignableTo(value.getClass(), Entity.class);
	}
}
