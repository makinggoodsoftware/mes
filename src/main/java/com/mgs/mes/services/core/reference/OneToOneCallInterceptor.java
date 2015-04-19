package com.mgs.mes.services.core.reference;

import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.entity.factory.entity.entityData.EntityCallInterceptor;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.OneToOne;
import com.mgs.mes.services.core.EntityRetriever;
import org.bson.types.ObjectId;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

public class OneToOneCallInterceptor<T extends Entity> extends EntityCallInterceptor implements InvocationHandler, OneToOne<T> {
	private final EntityRetriever<T> retriever;

	public OneToOneCallInterceptor(EntityData entityData, EntityRetriever<T> retriever) {
		super(entityData);
		this.retriever = retriever;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().equals("retrieve")) {
			return retrieve();
		} else if (method.getName().equals("equals")) {
			Entity equalsRight = (Entity) args[0];
			return equals(equalsRight);
		} else if (method.getName().equals("getRefName")) {
			return getRefName();
		} else if (method.getName().equals("getRefId")) {
			return getRefId();
		} else {
			return super.invoke(proxy, method, args);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		OneToOneCallInterceptor that = null;
		if (OneToOneCallInterceptor.class.isAssignableFrom(o.getClass())){
			that = (OneToOneCallInterceptor) o;
		}else if (OneToOne.class.isAssignableFrom(o.getClass())) {
			if (o instanceof Proxy){
				that = (OneToOneCallInterceptor) Proxy.getInvocationHandler(o);
			}
		}

		return that != null && entityData.equals(that.entityData);

	}

	@Override
	public T retrieve() {
		Optional<T> retrieved = retriever.byId(getRefId());
		if (!retrieved.isPresent()) throw new IllegalStateException("Corrupted state in the DB, can't access the related object");
		return retrieved.get();
	}

	@Override
	public String getRefName() {
		return (String) entityData.get("getRefName");
	}

	@Override
	public ObjectId getRefId() {
		return (ObjectId) entityData.get("getRefId");
	}
}
