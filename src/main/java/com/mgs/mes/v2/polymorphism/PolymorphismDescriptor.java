package com.mgs.mes.v2.polymorphism;

import java.util.List;

public class PolymorphismDescriptor {
	private final Class type;
	private final List<Class> polymorphicTypes;

	public PolymorphismDescriptor(Class type, List<Class> polymorphicTypes) {
		this.type = type;
		this.polymorphicTypes = polymorphicTypes;
	}

	public Class getType() {
		return type;
	}

	public List<Class> getPolymorphicTypes() {
		return polymorphicTypes;
	}
}
