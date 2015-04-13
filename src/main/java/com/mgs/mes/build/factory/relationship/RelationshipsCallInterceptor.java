package com.mgs.mes.build.factory.relationship;

import com.mgs.mes.context.MongoContext;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.Relationship;
import com.mgs.mes.model.RelationshipBuilder;
import com.mgs.mes.model.Relationships;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RelationshipsCallInterceptor <T extends Entity> implements InvocationHandler, Relationships<T> {
	private final MongoContext mongoContext;

	private Relationships<T> proxy;
	private T sourceValue;

	public RelationshipsCallInterceptor(MongoContext mongoContext) {
		this.mongoContext = mongoContext;
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
			return generateRelationshipBuilder ();
		}
	}

	private <B extends Entity, Y extends Relationship<T, B>>
	RelationshipBuilder<T, B, Y> generateRelationshipBuilder() {
		throw new NotImplementedException();
	}

	@Override
	public Relationships<T> from(T from) {
		this.sourceValue = from;
		return proxy;
	}
}
