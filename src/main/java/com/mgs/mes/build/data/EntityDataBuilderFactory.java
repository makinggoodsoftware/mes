package com.mgs.mes.build.data;

import com.mgs.mes.model.Entity;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.FieldAccessorParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class EntityDataBuilderFactory {
	private final EntityDataFactory entityDataFactory;
	private final BeanNamingExpert beanNamingExpert;
	private final FieldAccessorParser fieldAccessorParser;

	public EntityDataBuilderFactory(EntityDataFactory entityDataFactory, BeanNamingExpert beanNamingExpert, FieldAccessorParser fieldAccessorParser) {
		this.entityDataFactory = entityDataFactory;
		this.beanNamingExpert = beanNamingExpert;
		this.fieldAccessorParser = fieldAccessorParser;
	}

	public <T extends Entity> EntityDataBuilder empty(Class<T> type){
		return from(type, null);
	}

	public <T extends Entity> EntityDataBuilder from(Class<T> type, T baseLine){
		Stream<FieldAccessor> accessors = fieldAccessorParser.parse(type);
		Map<FieldAccessor, Object> fieldsByGetterMethodName = new HashMap<>();
		accessors.forEach(accessor -> fieldsByGetterMethodName.put(accessor, findBaseLineValue (baseLine, accessor)));

		return new EntityDataBuilder(entityDataFactory, beanNamingExpert, fieldAccessorParser, type, fieldsByGetterMethodName);
	}

	private <T extends Entity> Object findBaseLineValue(T baseLine, FieldAccessor accessor) {
		try {
			if (baseLine == null) {
				return accessor.getFieldName().equals("id") ? Optional.empty() : null;
			}

			Method accessorMethod = baseLine.getClass().getMethod(accessor.getMethodName());
			return accessorMethod.invoke(baseLine);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
