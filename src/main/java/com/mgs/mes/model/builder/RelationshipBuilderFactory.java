package com.mgs.mes.model.builder;

import com.mgs.mes.model.data.ModelData;
import com.mgs.mes.model.data.ModelDataBuilderFactory;
import com.mgs.mes.model.entity.Entity;
import com.mgs.mes.model.entity.Relationship;
import com.mgs.mes.model.entity.RelationshipBuilder;
import com.mgs.mes.model.factory.ModelFactory;
import com.mgs.mes.model.relationships.EntityReference;
import com.mgs.mes.model.relationships.MongoReferenceFactory;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;

public class RelationshipBuilderFactory
<A extends Entity, B extends Entity, T extends Relationship<A, B>, Z extends RelationshipBuilder<T, A, B>>
extends EntityBuilderFactory<T,Z>{
	private final MongoReferenceFactory mongoReferenceFactory;

	public RelationshipBuilderFactory(ModelDataBuilderFactory modelDataBuilderFactory, FieldAccessorParser fieldAccessorParser, BeanNamingExpert beanNamingExpert, ModelFactory<ModelData> modelFactory, MongoReferenceFactory mongoReferenceFactory, Class<T> modelType, Class<Z> modelBuilderType) {
		super(modelDataBuilderFactory, fieldAccessorParser, beanNamingExpert, modelFactory, modelType, modelBuilderType);
		this.mongoReferenceFactory = mongoReferenceFactory;
	}

	@SuppressWarnings("unchecked")
	public Z newRelationshipBuilder(A relationshipLeft, B relationshipRight) {
		Z builder = newEntityBuilder();
		EntityReference<A> referenceLeft = mongoReferenceFactory.newReference(relationshipLeft);
		EntityReference<B> referenceRight = mongoReferenceFactory.newReference(relationshipRight);
		return (Z) builder.withLeft(referenceLeft).withRight(referenceRight);
	}

}
