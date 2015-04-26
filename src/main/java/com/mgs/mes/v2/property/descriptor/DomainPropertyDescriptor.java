package com.mgs.mes.v2.property.descriptor;

import com.mgs.mes.model.Entity;
import com.mgs.mes.v2.property.manager.DomainPropertyManager;
import com.mgs.mes.v2.property.type.domain.DomainPropertyType;

import java.util.List;

public class DomainPropertyDescriptor {
	private final DomainPropertyType domainPropertyType;
	private final DomainPropertyManager domainPropertyManager;
	private final Class<? extends Entity> type;
	private final List<Class<? extends Entity>> polymorphicType;

	public DomainPropertyDescriptor(DomainPropertyType domainPropertyType, DomainPropertyManager domainPropertyManager, Class<? extends Entity> type, List<Class<? extends Entity>> polymorphicType) {
		this.domainPropertyType = domainPropertyType;
		this.domainPropertyManager = domainPropertyManager;
		this.type = type;
		this.polymorphicType = polymorphicType;
	}

	public DomainPropertyType getDomainPropertyType() {
		return domainPropertyType;
	}

	public DomainPropertyManager getDomainPropertyManager() {
		return domainPropertyManager;
	}

	public Class<? extends Entity> getJavaType() {
		return type;
	}

	public List<Class<? extends Entity>> getPolymorphicTypes() {
		return polymorphicType;
	}
}
