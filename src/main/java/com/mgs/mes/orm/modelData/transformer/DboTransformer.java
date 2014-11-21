package com.mgs.mes.orm.modelData.transformer;

import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.orm.ModelData;
import com.mgs.mes.orm.modelFactory.DynamicModelFactory;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.FieldAccessorParser;
import com.mongodb.DBObject;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DboTransformer implements ModelDataTransformer<DBObject> {
	private final BeanNamingExpert beanNamingExpert;
	private final FieldAccessorParser fieldAccessorParser;
	private final DynamicModelFactory dynamicModelFactory;

	public DboTransformer(DynamicModelFactory dynamicModelFactory, BeanNamingExpert beanNamingExpert, FieldAccessorParser fieldAccessorParser) {
		this.beanNamingExpert = beanNamingExpert;
		this.fieldAccessorParser = fieldAccessorParser;
		this.dynamicModelFactory = dynamicModelFactory;
	}

	@SuppressWarnings("Convert2MethodRef")
	public ModelData transform (Class<? extends MongoEntity> type, DBObject dbObject) {
		Map<String, Object> fieldValuesByGetterName = asStream(dbObject).collect(Collectors.toMap(
				fieldByGetterMethod -> buildKey(fieldByGetterMethod.getKey()),
				fieldByGetterMethod -> buildValue(type, fieldByGetterMethod)
		));
		return new ModelData(dbObject, fieldValuesByGetterName);
	}

	private <T extends MongoEntity> Object buildValue(Class<T> type, Map.Entry<String, Object> valueByFieldName) {
		Object rawValue = valueByFieldName.getValue();
		String fieldName = valueByFieldName.getKey();

		boolean isNestedEntity = DBObject.class.isAssignableFrom(rawValue.getClass());
		return isNestedEntity ?
				extractNestedValue (type, fieldName, (DBObject) rawValue) :
				rawValue;
	}

	private <T extends MongoEntity> Object extractNestedValue(Class<T> type, String fieldName, DBObject nestedValue) {
		String getterName = beanNamingExpert.getGetterName(fieldName);
		FieldAccessor accessor = fieldAccessorParser.parse(type, getterName).get();
		//noinspection unchecked
		Class<MongoEntity> nestedType = (Class<MongoEntity>) accessor.getDeclaredType();
		return dynamicModelFactory.dynamicModel(nestedType, transform(nestedType, nestedValue));
	}

	private String buildKey(String fieldName) {
		return beanNamingExpert.getGetterName(fieldName);
	}

	private Stream<Map.Entry<String, Object>> asStream(DBObject dbObject) {
		Map map = dbObject.toMap();
		//noinspection unchecked
		Map<String, Object> castedMap = (Map<String, Object>) map;

		return castedMap.entrySet().stream();
	}
}
