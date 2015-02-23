package com.mgs.mes.model.relationships;

import com.mgs.mes.model.builder.RelationshipBuilderFactory;
import com.mgs.mes.model.entity.Entity;
import com.mgs.mes.model.entity.RelationshipBuilder;
import com.mgs.mes.model.entity.Relationships;

import java.util.Map;

import static java.lang.reflect.Proxy.newProxyInstance;

public class RelationshipsFactory<T extends Entity, Y extends Relationships<T>> {
	private final Class<Y> relationshipType;
	private final Map<Class<? extends RelationshipBuilder>, RelationshipBuilderFactory> relationshipBuilderFactoryByRelationship;

	public RelationshipsFactory(Class<Y> relationshipType, Map<Class<? extends RelationshipBuilder>, RelationshipBuilderFactory> relationshipBuilderFactoryByRelationship) {
		this.relationshipType = relationshipType;
		this.relationshipBuilderFactoryByRelationship = relationshipBuilderFactoryByRelationship;
	}

	public Y from(T from) {
		//noinspection unchecked
		Y proxy = (Y) newProxyInstance(
				RelationshipsFactory.class.getClassLoader(),
				new Class[]{relationshipType},
				new RelationshipsCallInterceptor(from, relationshipBuilderFactoryByRelationship)
		);
		//noinspection unchecked
		return (Y) proxy.from(from);
	}
}
