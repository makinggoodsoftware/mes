package com.mgs.mes.v2.property.manager.impl;

import com.mgs.mes.model.Entity;
import com.mgs.mes.v2.property.manager.DomainPropertyManager;
import com.mgs.mes.v2.property.type.dbo.DboPropertyType;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.FieldAccessorParser;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.function.Function;

public class BasePropertyManager implements DomainPropertyManager {
	private final BeanNamingExpert beanNamingExpert;
	private final FieldAccessorParser fieldAccessorParser;
	private final Function<FieldAccessor, Boolean> function;

	public BasePropertyManager(BeanNamingExpert beanNamingExpert, FieldAccessorParser fieldAccessorParser, Function<FieldAccessor, Boolean> function) {
		this.beanNamingExpert = beanNamingExpert;
		this.fieldAccessorParser = fieldAccessorParser;
		this.function = function;
	}

	@Override
	public boolean applies(Class<? extends Entity> parentType, String key) {
		String getterName = beanNamingExpert.getGetterName(key);
		FieldAccessor fieldAccessor = fieldAccessorParser.parse(parentType, getterName).get();
		return function.apply(fieldAccessor);
	}

	@Override
	public Object enrich(DboPropertyType dboPropertyType, Object toEnrich) {
		throw new NotImplementedException();
	}

}
