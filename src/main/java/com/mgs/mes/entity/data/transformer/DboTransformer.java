package com.mgs.mes.entity.data.transformer;

import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.entity.factory.entity.EntityFactory;
import com.mgs.mes.model.Entity;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.FieldAccessorParser;
import com.mgs.reflection.Reflections;
import com.mongodb.DBObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class DboTransformer implements EntityDataTransformer<DBObject> {
	private final BeanNamingExpert beanNamingExpert;
	private final FieldAccessorParser fieldAccessorParser;
	private final EntityFactory<EntityData> entityFactory;
	private final Reflections reflections;

	public DboTransformer(EntityFactory<EntityData> entityFactory, BeanNamingExpert beanNamingExpert, FieldAccessorParser fieldAccessorParser, Reflections reflections) {
		this.beanNamingExpert = beanNamingExpert;
		this.fieldAccessorParser = fieldAccessorParser;
		this.entityFactory = entityFactory;
		this.reflections = reflections;
	}

	@SuppressWarnings("Convert2MethodRef")
	public EntityData transform (Class<? extends Entity> type, DBObject dbObject) {
		return doTransform(type, dbObject, true);
	}

	private EntityData doTransform(Class<? extends Entity> type, DBObject dbObject, boolean isOuter) {
		if (isNestedDboObject(dbObject)){
			assertNoIdField(dbObject);
			assertInnerObjectHasNo_Id(dbObject, isOuter);
		}

		Map<String, Object> fieldValuesByGetterName = asStream(dbObject).
		filter((dbObjectFieldNameAndValueEntry) -> {
			String dboFieldName = dbObjectFieldNameAndValueEntry.getKey();
			//noinspection SimplifiableIfStatement
			if (dboFieldName.equals("_id")) return true;
			return fieldAccessorParser.
					parse(type).
					filter((srcFieldAccessor) -> srcFieldAccessor.getFieldName().equals(dboFieldName)).
					collect(Collectors.toList())
					.size() > 0;
		}).
		collect(Collectors.toMap(
				fieldByGetterMethod -> buildKey(fieldByGetterMethod.getKey()),
				fieldByGetterMethod -> buildValue(type, fieldByGetterMethod, isOuter)
		));
		if (fieldValuesByGetterName.get("getId") == null){
			fieldValuesByGetterName.put("getId", empty());
		}
		return new EntityData(dbObject, fieldValuesByGetterName);
	}

	private void assertInnerObjectHasNo_Id(DBObject dbObject, boolean isOuter) {
		if (!isOuter){
			if (dbObject.get("_id") != null) throw new IllegalArgumentException("Inner object can't have and ID!");
		}
	}

	private void assertNoIdField(DBObject dbObject) {
		if (dbObject.get("id") != null) throw new IllegalArgumentException("id is an invalid property for the dbo, it has to be _id");
	}

	private <T extends Entity> Object buildValue(Class<T> type, Map.Entry<String, Object> valueByFieldName, boolean isOuter) {
		Object rawValue = valueByFieldName.getValue();
		String fieldName = valueByFieldName.getKey();

		boolean isId = fieldName.equals("_id");
		if (!isOuter && isId) return empty();
		if (isId) return of(rawValue);

		return isNestedDboObject(rawValue) ?
				extractNestedValue(type, fieldName, (DBObject) rawValue) :
				rawValue;
	}

	private boolean isNestedDboObject(Object rawValue) {
		return reflections.isAssignableTo(rawValue.getClass(), DBObject.class) &&
		! (reflections.isAssignableTo(rawValue.getClass(), List.class));
	}

	private <T extends Entity> Object extractNestedValue(Class<T> type, String fieldName, DBObject nestedValue) {
		String getterName = beanNamingExpert.getGetterName(fieldName);
		FieldAccessor accessor = fieldAccessorParser.parse(type, getterName).get();
		//noinspection unchecked
		Class<Entity> nestedType = (Class<Entity>) accessor.getDeclaredType();
		return entityFactory.from(nestedType, doTransform(nestedType, nestedValue, false));
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
