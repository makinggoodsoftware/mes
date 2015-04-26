package com.mgs.mes.v2.polymorphism;

import com.mgs.mes.model.Entity;

import java.util.List;

public class PolymorphismDescriptor {
	private Class<? extends Entity> type;
	private List<Class<? extends Entity>> polymorphicTypes;

	public Class<? extends Entity> getType() {
		return type;
	}

	public List<Class<? extends Entity>> getPolymorphicTypes() {
		return polymorphicTypes;
	}
}
