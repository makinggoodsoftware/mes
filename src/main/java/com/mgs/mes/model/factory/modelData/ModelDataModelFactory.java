package com.mgs.mes.model.factory.modelData;

import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.model.data.ModelData;
import com.mgs.mes.model.factory.ModelFactory;

import static java.lang.reflect.Proxy.newProxyInstance;

public class ModelDataModelFactory implements ModelFactory<ModelData> {
	@Override
	public <T extends MongoEntity> T from(Class<T> modelType, ModelData modelData) {
		//noinspection unchecked
		return (T) newProxyInstance(
				ModelDataModelFactory.class.getClassLoader(),
				new Class[]{modelType},
				new ModelCallInterceptor(modelData)
		);
	}
}
