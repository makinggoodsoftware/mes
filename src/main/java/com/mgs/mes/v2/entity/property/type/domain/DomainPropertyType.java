package com.mgs.mes.v2.entity.property.type.domain;

import com.mgs.mes.v2.entity.property.type.dbo.DboPropertyType;

import static com.mgs.mes.v2.entity.property.type.domain.MultiplicityType.COMPOSITE;
import static com.mgs.mes.v2.entity.property.type.domain.MultiplicityType.SINGLE;

public enum DomainPropertyType {
	VALUE(
			SINGLE,
			DboPropertyType.VALUE
	),
	LIST_OF_VALUES(
			COMPOSITE,
			DboPropertyType.VALUE
	),
	ENTITY(
			SINGLE,
			DboPropertyType.ENTITY
	),
	LIST_OF_ENTITIES(
			COMPOSITE,
			DboPropertyType.ENTITY
	);


	private final MultiplicityType multiplicityType;
	private final DboPropertyType valueDescriptor;

	DomainPropertyType(MultiplicityType multiplicityType, DboPropertyType valueDescriptor) {
		this.multiplicityType = multiplicityType;
		this.valueDescriptor = valueDescriptor;
	}

	public MultiplicityType getMultiplicityType() {
		return multiplicityType;
	}

	public DboPropertyType getValueDescriptor() {
		return valueDescriptor;
	}

}
