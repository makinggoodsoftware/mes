package com.mgs.mes.build.factory.relationship;

import com.mgs.mes.build.factory.builder.RelationshipBuilderFactory;
import com.mgs.mes.context.MongoContext;
import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.meta.utils.Entities;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.Relationship;
import com.mgs.mes.model.RelationshipBuilder;
import com.mgs.mes.model.Relationships;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RelationshipsCallInterceptor <T extends Entity> implements InvocationHandler, Relationships<T> {
	private final MongoContext mongoContext;
	private final Entities entities;

	private Relationships<T> proxy;
	private T sourceValue;

	public RelationshipsCallInterceptor(MongoContext mongoContext, Entities entities) {
		this.mongoContext = mongoContext;
		this.entities = entities;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return doInvoke(proxy, method, args);
	}

	public <B extends Entity>
	Object doInvoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (this.proxy == null){
			//noinspection unchecked
			this.proxy = (Relationships<T>) proxy;
		}
		if (method.getName().equals("from")) {
			//noinspection unchecked
			T fromValue = (T) args[0];
			return from(fromValue);
		} else {
			//noinspection unchecked
			return generateRelationshipBuilder (method, sourceValue, (B) args[0]);
		}
	}

	private <B extends Entity, Y extends Relationship<T, B>>
	RelationshipBuilder<T, B, Y> generateRelationshipBuilder(Method method, T fromRelationship, B toRelationship) {
		//noinspection unchecked
		Class<? extends RelationshipBuilder<T, B, Y>> relationshipToBuild = (Class<? extends RelationshipBuilder<T, B, Y>>) method.getReturnType();
		RelationshipBuilderFactory<T, B, Y, RelationshipBuilder<T, B, Y>> relationshipBuilderFactory = mongoContext.getRelationshipBuilderFactory(relationshipToBuild);
		//noinspection unchecked
		Class<T> leftType = (Class<T>) entities.findBaseType(fromRelationship.getClass(), Entity.class);
		//noinspection unchecked
		Class<B> rightType = (Class<B>) entities.findBaseType(toRelationship.getClass(), Entity.class);
		EntityRetriever<T> retrieverLeft = mongoContext.getRetriever(leftType);
		EntityRetriever<B> retrieverRight = mongoContext.getRetriever(rightType);
		return relationshipBuilderFactory.newRelationshipBuilder(fromRelationship, retrieverLeft, toRelationship, retrieverRight);
	}

	@Override
	public Relationships<T> from(T from) {
		this.sourceValue = from;
		return proxy;
	}
}
