package com.mgs.mes.v2.polymorphism;

import com.mgs.mes.model.Entity;
import com.mgs.mes.model.OneToMany;
import com.mgs.mes.model.OneToOne;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.FieldAccessorParser;
import com.mgs.reflection.Reflections;

import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;

public class PolymorphismManager {
	private final FieldAccessorParser fieldAccessorParser;
	private final BeanNamingExpert beanNamingExpert;
	private final Reflections reflections;

	public PolymorphismManager(FieldAccessorParser fieldAccessorParser, BeanNamingExpert beanNamingExpert, Reflections reflections) {
		this.fieldAccessorParser = fieldAccessorParser;
		this.beanNamingExpert = beanNamingExpert;
		this.reflections = reflections;
	}

	public PolymorphismDescriptor analise(Class<? extends Entity> parentType, String key) {
		String accesorName = beanNamingExpert.getGetterName(key);
		FieldAccessor fieldAccessor = fieldAccessorParser.parse(parentType, accesorName).get();
		Class<?> declaredType = fieldAccessor.getDeclaredType();
		if (isParametrized(declaredType)) {
//			declaredType = fieldAccessor.getParsedTypes().get(0).getSpecificClass().get();
			declaredType = null;
		}
		return new PolymorphismDescriptor(declaredType, singletonList(declaredType));
	}

	public Class<? extends Entity> resolve(List<Class> polymorphicTypes, Object value) {
		throw new IllegalStateException("Polymorphism is not yet fully supported");
	}

	private boolean isParametrized(Class<?> declaredType) {
		return
				reflections.isAssignableTo(declaredType, Collection.class) ||
				reflections.isAssignableTo(declaredType, OneToOne.class) ||
				reflections.isAssignableTo(declaredType, OneToMany.class);
	}
}
