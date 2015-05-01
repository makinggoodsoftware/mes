package com.mgs.mes.v2.entity.property.manager;

import com.mgs.mes.model.Entity;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.FieldAccessorParser;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Optional;
import java.util.function.Function;

public class BasePropertyManager implements DomainPropertyManager {
	private final BeanNamingExpert beanNamingExpert;
	private final FieldAccessorParser fieldAccessorParser;
	private final Function<FieldAccessor, Boolean> function;
	private final Optional<PropertyEnricher> enricher;

	public BasePropertyManager(BeanNamingExpert beanNamingExpert, FieldAccessorParser fieldAccessorParser, Function<FieldAccessor, Boolean> function, Optional<PropertyEnricher> enricher) {
		this.beanNamingExpert = beanNamingExpert;
		this.fieldAccessorParser = fieldAccessorParser;
		this.function = function;
		this.enricher = enricher;
	}

	@Override
	public boolean applies(Class<? extends Entity> parentType, String key) {
		String getterName = beanNamingExpert.getGetterName(key);
		FieldAccessor fieldAccessor = fieldAccessorParser.parse(parentType, getterName).get();
		return function.apply(fieldAccessor);
	}

	@Override
	public Object enrich(Class type, Object toEnrich) {
		if (!enricher.isPresent()) throw new NotImplementedException();
		return enricher.get().enrich (type, toEnrich);
	}

}
