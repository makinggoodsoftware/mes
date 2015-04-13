package com.mgs.mes.build.factory.builder;

import com.mgs.mes.build.data.EntityData;
import com.mgs.mes.build.data.EntityDataBuilderFactory;
import com.mgs.mes.build.factory.entity.EntityFactory;
import com.mgs.mes.build.factory.reference.EntityReferenceFactory;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityReference;
import com.mgs.mes.model.Relationship;
import com.mgs.mes.model.RelationshipBuilder;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mgs.reflection.Reflections;

public class RelationshipBuilderFactory
<A extends Entity, B extends Entity, T extends Relationship<A, B>, Z extends RelationshipBuilder<A, B, T>>
extends EntityBuilderFactory<T , Z>{
	private final EntityReferenceFactory entityReferenceFactory;

	public RelationshipBuilderFactory(EntityDataBuilderFactory entityDataBuilderFactory, FieldAccessorParser fieldAccessorParser, BeanNamingExpert beanNamingExpert, EntityFactory<EntityData> entityFactory, EntityReferenceFactory entityReferenceFactory, Class<T> modelType, Class<Z> modelBuilderType, Reflections reflections) {
		super(entityDataBuilderFactory, fieldAccessorParser, beanNamingExpert, entityFactory, modelType, modelBuilderType, reflections, entityReferenceFactory);
		this.entityReferenceFactory = entityReferenceFactory;
	}

	public Z newRelationshipBuilder(A relationshipLeft, B relationshipRight) {
		Z builder = newEntityBuilder();
		EntityReference<A> referenceLeft = entityReferenceFactory.newReference(relationshipLeft);
		EntityReference<B> referenceRight = entityReferenceFactory.newReference(relationshipRight);
		//noinspection unchecked
		return (Z) builder.withLeft(referenceLeft).withRight(referenceRight);
	}


}
