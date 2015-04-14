package com.mgs.mes.entity.data;

import com.mgs.mes.model.Entity;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.FieldAccessorParser;

import java.util.Map;

public class EntityDataBuilder {
	private final EntityDataFactory entityDataFactory;
	private final BeanNamingExpert beanNamingExpert;
	private final FieldAccessorParser fieldAccessorParser;
	private final Class<? extends Entity> type;
	private final Map<FieldAccessor, Object> fieldValuesByAccessor;

	public EntityDataBuilder(EntityDataFactory entityDataFactory, BeanNamingExpert beanNamingExpert, FieldAccessorParser fieldAccessorParser, Class<? extends Entity> type, Map<FieldAccessor, Object> fieldValuesByAccessor) {
		this.entityDataFactory = entityDataFactory;
		this.beanNamingExpert = beanNamingExpert;
		this.fieldAccessorParser = fieldAccessorParser;
		this.type = type;
		this.fieldValuesByAccessor = fieldValuesByAccessor;
	}

	public EntityData build() {
		return entityDataFactory.fromFieldAccessorMap(type, fieldValuesByAccessor);
	}

	public EntityDataBuilder with (String fieldName, Object value) {
		String getterName = beanNamingExpert.getGetterName(fieldName);
		FieldAccessor fieldAccessor = fieldAccessorParser.parse(type, getterName).orElseThrow(
			() -> new IllegalArgumentException("There isn't an accessor associated to the field name: " + fieldName)
		);

		return with(fieldAccessor, value);
	}

	public EntityDataBuilder with(FieldAccessor fieldAccessor, Object value) {
		if (!fieldValuesByAccessor.containsKey(fieldAccessor)) {
			throw new IllegalStateException("The map for the builder doesn't include the accessor for: " + fieldAccessor.getFieldName());
		}

		fieldValuesByAccessor.put(fieldAccessor, value);
		return this;
	}
}
