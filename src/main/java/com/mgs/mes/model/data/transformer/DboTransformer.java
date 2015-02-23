package com.mgs.mes.model.data.transformer;

import com.mgs.mes.model.data.ModelData;
import com.mgs.mes.model.entity.Entity;
import com.mgs.mes.model.factory.ModelFactory;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.FieldAccessorParser;
import com.mongodb.DBObject;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class DboTransformer implements ModelDataTransformer<DBObject> {
	private final BeanNamingExpert beanNamingExpert;
	private final FieldAccessorParser fieldAccessorParser;
	private final ModelFactory<ModelData> modelFactory;

	public DboTransformer(ModelFactory<ModelData> modelFactory, BeanNamingExpert beanNamingExpert, FieldAccessorParser fieldAccessorParser) {
		this.beanNamingExpert = beanNamingExpert;
		this.fieldAccessorParser = fieldAccessorParser;
		this.modelFactory = modelFactory;
	}

	@SuppressWarnings("Convert2MethodRef")
	public ModelData transform (Class<? extends Entity> type, DBObject dbObject) {
		return doTransform(type, dbObject, true);
	}

	private ModelData doTransform(Class<? extends Entity> type, DBObject dbObject, boolean isOuter) {
		assertNoIdField(dbObject);
		assertInnerObjectHasNo_Id(dbObject, isOuter);

		Map<String, Object> fieldValuesByGetterName = asStream(dbObject).collect(Collectors.toMap(
				fieldByGetterMethod -> buildKey(fieldByGetterMethod.getKey()),
				fieldByGetterMethod -> buildValue(type, fieldByGetterMethod, isOuter)
		));
		if (fieldValuesByGetterName.get("getId") == null){
			fieldValuesByGetterName.put("getId", empty());
		}
		return new ModelData(dbObject, fieldValuesByGetterName);
	}

	private void assertInnerObjectHasNo_Id(DBObject dbObject, boolean isOuter) {
		if (!isOuter){
			if (dbObject.get("_id") != null) throw new IllegalArgumentException("Inner object can't have and ID!");
		}
	}

	private void assertNoIdField(DBObject dbObject) {
		if (dbObject.get("id") != null) throw new IllegalArgumentException("id is an invalid property for the dbo, it has to be id");
	}

	private <T extends Entity> Object buildValue(Class<T> type, Map.Entry<String, Object> valueByFieldName, boolean isOuter) {
		Object rawValue = valueByFieldName.getValue();
		String fieldName = valueByFieldName.getKey();

		boolean isId = fieldName.equals("_id");
		if (!isOuter && isId) return empty();
		if (isId) return of(rawValue);

		boolean isNestedEntity = DBObject.class.isAssignableFrom(rawValue.getClass());
		return !isNestedEntity ?
				rawValue :
				extractNestedValue(type, fieldName, (DBObject) rawValue);
	}

	private <T extends Entity> Object extractNestedValue(Class<T> type, String fieldName, DBObject nestedValue) {
		String getterName = beanNamingExpert.getGetterName(fieldName);
		FieldAccessor accessor = fieldAccessorParser.parse(type, getterName).get();
		//noinspection unchecked
		Class<Entity> nestedType = (Class<Entity>) accessor.getDeclaredType();
		return modelFactory.from(nestedType, doTransform(nestedType, nestedValue, false));
	}

	private String buildKey(String fieldName) {
		if (fieldName.equals("_id")) return "getId";
		return beanNamingExpert.getGetterName(fieldName);
	}

	private Stream<Map.Entry<String, Object>> asStream(DBObject dbObject) {
		Map map = dbObject.toMap();
		//noinspection unchecked
		Map<String, Object> castedMap = (Map<String, Object>) map;

		return castedMap.entrySet().stream();
	}
}
