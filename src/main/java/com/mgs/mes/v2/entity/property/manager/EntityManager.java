package com.mgs.mes.v2.entity.property.manager;

import com.mgs.mes.model.Entity;
import com.mgs.mes.model.OneToMany;
import com.mgs.mes.model.OneToOne;
import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.Reflections;

import java.util.Collection;

public class EntityManager {
	private final Reflections reflections;

	public EntityManager(Reflections reflections) {
		this.reflections = reflections;
	}

	public boolean isSimpleValue(FieldAccessor fieldAccessor) {
		return isSimpleValue(fieldAccessor.getDeclaredType());
	}

	public boolean isListOfEntities(FieldAccessor fieldAccessor) {
		return reflections.isAssignableTo(fieldAccessor.getDeclaredType(), Collection.class) &&
				fieldAccessor.getParsedTypes().size() == 1;
	}

	public boolean isSimpleEntity(FieldAccessor fieldAccessor) {
		return reflections.isAssignableTo(fieldAccessor.getDeclaredType(), Entity.class) &&
				!isOneToOne(fieldAccessor) &&
				!isOneToMany(fieldAccessor);
	}

	public boolean isListOfValues(FieldAccessor fieldAccessor) {
		return reflections.isAssignableTo(fieldAccessor.getDeclaredType(), Collection.class) &&
				fieldAccessor.getParsedTypes().size() == 1;
	}

	public boolean isOneToOne(FieldAccessor fieldAccessor) {
		return reflections.isAssignableTo(fieldAccessor.getDeclaredType(), OneToOne.class);
	}

	public boolean isOneToMany(FieldAccessor fieldAccessor) {
		return reflections.isAssignableTo(fieldAccessor.getDeclaredType(), OneToMany.class);
	}

	private boolean isSimpleValue(Class<?> declaredType) {
		return reflections.isSimple(declaredType);
	}
}
