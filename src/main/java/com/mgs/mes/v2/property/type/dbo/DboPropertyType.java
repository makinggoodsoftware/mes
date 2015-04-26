package com.mgs.mes.v2.property.type.dbo;

import java.util.Optional;

import static com.mgs.mes.v2.property.type.dbo.RequiresManipulation.NO_MANIPULATION;
import static com.mgs.mes.v2.property.type.dbo.RequiresManipulation.REQUIRES_MANIPULATION;
import static java.util.Optional.of;

public enum DboPropertyType {
	VALUE(
			NO_MANIPULATION,
			of(ComplexityType.VALUE),
			Optional.<EnrichedType>empty()
	),
	ENTITY(
			NO_MANIPULATION,
			of(ComplexityType.SIMPLE_ENTITY),
			Optional.<EnrichedType>empty()
	),
	ONE_TO_ONE(
			REQUIRES_MANIPULATION,
			Optional.<ComplexityType>empty(),
			Optional.of(EnrichedType.ONE_TO_ONE)
	),
	ONE_TO_MANY(
			REQUIRES_MANIPULATION,
			Optional.<ComplexityType>empty(),
			Optional.of(EnrichedType.ONE_TO_MANY)
	);

	private final RequiresManipulation requiresManipulation;
	private final Optional<ComplexityType> complexityType;
	private final Optional<EnrichedType> enrichedType;


	DboPropertyType(RequiresManipulation requiresManipulation, Optional<ComplexityType> complexityType, Optional<EnrichedType> enrichedType) {
		this.requiresManipulation = requiresManipulation;
		this.complexityType = complexityType;
		this.enrichedType = enrichedType;
	}



	public Optional<ComplexityType> getComplexityType() {
		return complexityType;
	}

	public Optional<EnrichedType> getEnrichedType() {
		return enrichedType;
	}

	public RequiresManipulation getRequiresManipulation() {
		return requiresManipulation;
	}
}
