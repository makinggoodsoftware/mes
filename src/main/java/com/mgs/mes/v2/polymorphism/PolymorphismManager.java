package com.mgs.mes.v2.polymorphism;

import com.mgs.mes.model.Entity;

import java.util.List;

public class PolymorphismManager {
	public PolymorphismDescriptor analise(Class<? extends Entity> parentType, String key) {
		return null;
	}

	public Class<? extends Entity> resolve(List<Class<? extends Entity>> polymorphicTypes, Object value) {
		return null;
	}
}
