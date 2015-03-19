package com.mgs.mes.build.factory.builder;

import com.mgs.mes.build.data.EntityData;
import com.mgs.mes.build.data.EntityDataBuilderFactory;
import com.mgs.mes.build.factory.entity.EntityFactory;
import com.mgs.mes.build.factory.relationships.EntityReferenceFactory;
import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityReference;
import com.mgs.mes.model.Relationship;
import com.mgs.mes.model.RelationshipBuilder;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;

public class RelationshipBuilderFactory
<A extends Entity, B extends Entity, T extends Relationship<A, B>, Z extends RelationshipBuilder<T, A, B>>
extends EntityBuilderFactory<T,Z>{
	private final EntityReferenceFactory entityReferenceFactory;

	public RelationshipBuilderFactory(EntityDataBuilderFactory entityDataBuilderFactory, FieldAccessorParser fieldAccessorParser, BeanNamingExpert beanNamingExpert, EntityFactory<EntityData> entityFactory, EntityReferenceFactory entityReferenceFactory, Class<T> modelType, Class<Z> modelBuilderType) {
		super(entityDataBuilderFactory, fieldAccessorParser, beanNamingExpert, entityFactory, modelType, modelBuilderType);
		this.entityReferenceFactory = entityReferenceFactory;
	}

	@SuppressWarnings("unchecked")
	public Z newRelationshipBuilder(A relationshipLeft, EntityRetriever retrieverA, B relationshipRight, EntityRetriever retrieverB) {
		Z builder = newEntityBuilder();
		EntityReference<A> referenceLeft = entityReferenceFactory.newReference(relationshipLeft, retrieverA);
		EntityReference<B> referenceRight = entityReferenceFactory.newReference(relationshipRight, retrieverB);
		return (Z) builder.withLeft(referenceLeft).withRight(referenceRight);
	}


}
