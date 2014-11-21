package com.mgs.mes.orm.modelData;

import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.orm.ModelData;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.FieldAccessorParser;

import java.util.Map;

public class ModelDataBuilder {
	private final ModelDataFactory modelDataFactory;
	private final BeanNamingExpert beanNamingExpert;
	private final FieldAccessorParser fieldAccessorParser;
	private final Class<? extends MongoEntity> type;
	private final Map<FieldAccessor, Object> fieldValuesByAccessor;

	public ModelDataBuilder(ModelDataFactory modelDataFactory, BeanNamingExpert beanNamingExpert, FieldAccessorParser fieldAccessorParser, Class<? extends MongoEntity> type, Map<FieldAccessor, Object> fieldValuesByAccessor) {
		this.modelDataFactory = modelDataFactory;
		this.beanNamingExpert = beanNamingExpert;
		this.fieldAccessorParser = fieldAccessorParser;
		this.type = type;
		this.fieldValuesByAccessor = fieldValuesByAccessor;
	}

	public ModelData build() {
		return modelDataFactory.fromFieldAccessorMap(type, fieldValuesByAccessor);
	}

	public ModelDataBuilder with (String fieldName, Object value) {
		String getterName = beanNamingExpert.getGetterName(fieldName);
		FieldAccessor fieldAccessor = fieldAccessorParser.parse(type, getterName).orElseThrow(
			() -> new IllegalArgumentException("There isn't an accessor associated to the field name: " + fieldName)
		);

		return with(fieldAccessor, value);
	}

	public ModelDataBuilder with(FieldAccessor fieldAccessor, Object value) {
		if (!fieldValuesByAccessor.containsKey(fieldAccessor)) {
			throw new IllegalStateException("The map for the builder doesn't include the accessor for: " + fieldAccessor.getFieldName());
		}

		fieldValuesByAccessor.put(fieldAccessor, value);
		return this;
	}
}
