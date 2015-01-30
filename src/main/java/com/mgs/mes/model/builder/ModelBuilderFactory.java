package com.mgs.mes.model.builder;

import com.mgs.mes.model.ModelBuilder;
import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.model.data.ModelData;
import com.mgs.mes.model.data.ModelDataBuilder;
import com.mgs.mes.model.data.ModelDataBuilderFactory;
import com.mgs.mes.model.factory.ModelFactory;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;

import java.lang.reflect.Proxy;

public class ModelBuilderFactory<T extends MongoEntity, Z extends ModelBuilder<T>> {
	private final ModelDataBuilderFactory modelDataBuilderFactory;
	private final FieldAccessorParser fieldAccessorParser;
	private final BeanNamingExpert beanNamingExpert;
	private final Class<T> modelType;
	private final Class<Z> modelBuilderType;
	private final ModelFactory<ModelData> modelFactory;

	public ModelBuilderFactory(ModelDataBuilderFactory modelDataBuilderFactory, FieldAccessorParser fieldAccessorParser, BeanNamingExpert beanNamingExpert, ModelFactory<ModelData> modelFactory, Class<T> modelType, Class<Z> modelBuilderType) {
		this.modelDataBuilderFactory = modelDataBuilderFactory;
		this.fieldAccessorParser = fieldAccessorParser;
		this.beanNamingExpert = beanNamingExpert;
		this.modelType = modelType;
		this.modelBuilderType = modelBuilderType;
		this.modelFactory = modelFactory;
	}

	public Z createNew() {
		return newProxyInstance(modelDataBuilderFactory.empty(modelType));
	}

	public Z update(T baseLine) {
		return newProxyInstance(modelDataBuilderFactory.from(modelType, baseLine));

	}

	private Z newProxyInstance(ModelDataBuilder modelDataBuilder) {
		//noinspection unchecked
		return (Z) Proxy.newProxyInstance(
				this.getClass().getClassLoader(),
				new Class[]{modelBuilderType},
				new BuilderCallInterceptor<>(fieldAccessorParser, beanNamingExpert, modelType, modelDataBuilder, modelFactory)
		);
	}

}
