package com.mgs.mes.v2.entity.property.type.dbo;

import java.util.Optional;

import static java.util.Optional.of;

public enum DboPropertyType {
	VALUE(
			of(ComplexityType.VALUE),
			Optional.<EnrichedType>empty()
	),
	ENTITY(
			of(ComplexityType.SIMPLE_ENTITY),
			Optional.<EnrichedType>empty()
	),
	ONE_TO_ONE(
			Optional.<ComplexityType>empty(),
			Optional.of(EnrichedType.ONE_TO_ONE)
	),
	ONE_TO_MANY(
			Optional.<ComplexityType>empty(),
			Optional.of(EnrichedType.ONE_TO_MANY)
	);

	private final Optional<ComplexityType> complexityType;
	private final Optional<EnrichedType> enrichedType;


	DboPropertyType(Optional<ComplexityType> complexityType, Optional<EnrichedType> enrichedType) {
		this.complexityType = complexityType;
		this.enrichedType = enrichedType;
	}



	public Optional<ComplexityType> getComplexityType() {
		return complexityType;
	}

	public Optional<EnrichedType> getEnrichedType() {
		return enrichedType;
	}

}
