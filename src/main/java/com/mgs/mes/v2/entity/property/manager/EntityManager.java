package com.mgs.mes.v2.entity.property.manager;

import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.Reflections;

public class EntityManager {
	private final Reflections reflections;

	public EntityManager(Reflections reflections) {
		this.reflections = reflections;
	}

	public boolean isSimpleValue(FieldAccessor fieldAccessor) {
//		return isSimpleValue(fieldAccessor.getDeclaredType());
		return false;
	}

	public boolean isListOfEntities(FieldAccessor fieldAccessor) {
//		return reflections.isAssignableTo(fieldAccessor.getDeclaredType(), Collection.class) &&
//				fieldAccessor.getParsedTypes().size() == 1;
		return false;
	}

	public boolean isSimpleEntity(FieldAccessor fieldAccessor) {
//		return reflections.isAssignableTo(fieldAccessor.getDeclaredType(), Entity.class) &&
//				!isOneToOne(fieldAccessor) &&
//				!isOneToMany(fieldAccessor);
		return false;
	}

	public boolean isListOfValues(FieldAccessor fieldAccessor) {
//		return reflections.isAssignableTo(fieldAccessor.getDeclaredType(), Collection.class) &&
//				fieldAccessor.getParsedTypes().size() == 1;
		return false;
	}

	public boolean isOneToOne(FieldAccessor fieldAccessor) {
//		return reflections.isAssignableTo(fieldAccessor.getDeclaredType(), OneToOne.class);
		return false;
	}

	public boolean isOneToMany(FieldAccessor fieldAccessor) {
//		return reflections.isAssignableTo(fieldAccessor.getDeclaredType(), OneToMany.class);
		return false;
	}

	private boolean isSimpleValue(Class<?> declaredType) {
//		return reflections.isSimple(declaredType);
		return false;
	}
}
