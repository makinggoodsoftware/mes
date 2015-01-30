package com.mgs.mes.model.factory.modelData;

import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.model.data.ModelData;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

class ModelCallInterceptor implements InvocationHandler, MongoEntity {
	private final ModelData modelData;

	public ModelCallInterceptor(ModelData modelData) {
		this.modelData = modelData;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().equals("asDbo")) {
			return asDbo();
		} else if (method.getName().equals("equals")) {
			MongoEntity equalsRight = (MongoEntity) args[0];
			return equals(equalsRight);
		} else if (method.getName().equals("toString")) {
			return toString();
		} else {
			return modelData.get(method.getName());
		}
	}

	@Override
	public DBObject asDbo() {
		return modelData.getDbo();
	}

	@Override
	public Optional<ObjectId> getId() {
		//noinspection unchecked
		return (Optional<ObjectId>) modelData.get("getId");
	}

	@Override
	public String toString() {
		return modelData.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		ModelCallInterceptor that = null;
		if (ModelCallInterceptor.class.isAssignableFrom(o.getClass())){
			that = (ModelCallInterceptor) o;
		}else if (MongoEntity.class.isAssignableFrom(o.getClass())) {
			if (o instanceof Proxy){
				that = (ModelCallInterceptor) Proxy.getInvocationHandler(o);
			}
		}

		return that != null && modelData.equals(that.modelData);

	}

	@Override
	public int hashCode() {
		return modelData.hashCode();
	}
}