package com.mgs.mes.orm.modelFactory;

import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.orm.ModelData;

import static java.lang.reflect.Proxy.newProxyInstance;

public class DynamicModelFactory {
	public <T extends MongoEntity> T dynamicModel(Class<T> modelType, ModelData modelData) {
		//noinspection unchecked
		return (T) newProxyInstance(
				DynamicModelFactory.class.getClassLoader(),
				new Class[]{modelType},
				new ModelCallInterceptor(modelData)
		);
	}
}
