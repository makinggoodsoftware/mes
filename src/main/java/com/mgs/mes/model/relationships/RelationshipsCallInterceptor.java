package com.mgs.mes.model.relationships;

import com.mgs.mes.model.builder.RelationshipBuilderFactory;
import com.mgs.mes.model.entity.Entity;
import com.mgs.mes.model.entity.RelationshipBuilder;
import com.mgs.mes.model.entity.Relationships;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class RelationshipsCallInterceptor<T extends Entity> implements InvocationHandler, Relationships<T> {
	private T sourceValue;
	private Relationships<T> proxy;
	private final Map<Class<? extends RelationshipBuilder>, RelationshipBuilderFactory> modelBuildersByType;

	public RelationshipsCallInterceptor(T sourceValue, Map<Class<? extends RelationshipBuilder>, RelationshipBuilderFactory> modelBuildersByType) {
		this.sourceValue = sourceValue;
		this.modelBuildersByType = modelBuildersByType;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (this.proxy == null){
			//noinspection unchecked
			this.proxy = (Relationships<T>) proxy;
		}
		if (method.getName().equals("from")) {
			//noinspection unchecked
			T fromValue = (T) args[0];
			return from(fromValue);
		} else {
			return generateRelationshipBuilder (method, sourceValue, (Entity)args[0]);
		}
	}

	private <A extends Entity, B extends Entity> RelationshipBuilder generateRelationshipBuilder(Method method, A fromRelationship, B toRelationship) {
		//noinspection unchecked
		Class<? extends RelationshipBuilder<T, A, B>> entityToBuild = (Class<? extends RelationshipBuilder<T, A, B>>) method.getReturnType();
		RelationshipBuilderFactory entityBuilderFactory = this.modelBuildersByType.get(entityToBuild);
		//noinspection unchecked
		return entityBuilderFactory.newRelationshipBuilder(fromRelationship, toRelationship);
	}

	@Override
	public Relationships<T> from(T from) {
		this.sourceValue = from;
		return proxy;
	}
}
