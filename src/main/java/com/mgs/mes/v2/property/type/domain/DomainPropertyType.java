package com.mgs.mes.v2.property.type.domain;

import com.mgs.mes.v2.property.type.dbo.DboPropertyType;

import java.util.Optional;

import static com.mgs.mes.v2.property.type.domain.MultiplicityType.COMPOSITE;
import static com.mgs.mes.v2.property.type.domain.MultiplicityType.SINGLE;
import static java.util.Optional.of;

public enum DomainPropertyType {
	VALUE(
			SINGLE,
			DboPropertyType.VALUE,
			Optional.<DboPropertyType>empty()
	),
	LIST_OF_VALUES(
			COMPOSITE,
			DboPropertyType.VALUE,
			of(DboPropertyType.VALUE)
	),
	ENTITY(
			SINGLE,
			DboPropertyType.ENTITY,
			Optional.<DboPropertyType>empty()
	),
	LIST_OF_ENTITIES(
			COMPOSITE,
			DboPropertyType.ENTITY,
			of(DboPropertyType.VALUE)
	),
	ONE_TO_ONE_TYPE(
			SINGLE,
			DboPropertyType.ONE_TO_ONE,
			Optional.<DboPropertyType>empty()
	),
	ONE_TO_MANY_TYPE(
			COMPOSITE,
			DboPropertyType.ONE_TO_ONE,
			Optional.of(DboPropertyType.ONE_TO_MANY)
	);


	private final MultiplicityType multiplicityType;
	private final DboPropertyType valueDescriptor;
	private final Optional<DboPropertyType> groupDescriptor;

	DomainPropertyType(MultiplicityType multiplicityType, DboPropertyType valueDescriptor, Optional<DboPropertyType> groupDescriptor) {
		this.multiplicityType = multiplicityType;
		this.valueDescriptor = valueDescriptor;
		this.groupDescriptor = groupDescriptor;
	}

	public MultiplicityType getMultiplicityType() {
		return multiplicityType;
	}

	public DboPropertyType getValueDescriptor() {
		return valueDescriptor;
	}

	public Optional<DboPropertyType> getGroupDescriptor() {
		return groupDescriptor;
	}
}
