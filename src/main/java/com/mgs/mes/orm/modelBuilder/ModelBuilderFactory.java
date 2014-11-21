package com.mgs.mes.orm.modelBuilder;

import com.mgs.mes.model.ModelBuilder;
import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.orm.modelData.ModelDataBuilder;
import com.mgs.mes.orm.modelData.ModelDataBuilderFactory;
import com.mgs.mes.orm.modelFactory.DynamicModelFactory;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;

import java.lang.reflect.Proxy;

public class ModelBuilderFactory<T extends MongoEntity, Z extends ModelBuilder<T>> {
	private final ModelDataBuilderFactory modelDataBuilderFactory;
	private final FieldAccessorParser fieldAccessorParser;
	private final BeanNamingExpert beanNamingExpert;
	private final Class<T> modelType;
	private final Class<Z> modelBuilderType;
	private final DynamicModelFactory dynamicModelFactory;

	public ModelBuilderFactory(ModelDataBuilderFactory modelDataBuilderFactory, FieldAccessorParser fieldAccessorParser, BeanNamingExpert beanNamingExpert, Class<T> modelType, Class<Z> modelBuilderType, DynamicModelFactory dynamicModelFactory) {
		this.modelDataBuilderFactory = modelDataBuilderFactory;
		this.fieldAccessorParser = fieldAccessorParser;
		this.beanNamingExpert = beanNamingExpert;
		this.modelType = modelType;
		this.modelBuilderType = modelBuilderType;
		this.dynamicModelFactory = dynamicModelFactory;
	}

	public Z newEntityBuilder() {
		return newProxyInstance(modelDataBuilderFactory.empty(modelType));
	}

	public Z newEntityBuilderFrom(T baseLine) {
		return newProxyInstance(modelDataBuilderFactory.from(modelType, baseLine));

	}

	private Z newProxyInstance(ModelDataBuilder modelDataBuilder) {
		//noinspection unchecked
		return (Z) Proxy.newProxyInstance(
				this.getClass().getClassLoader(),
				new Class[]{modelBuilderType},
				new BuilderCallInterceptor<>(fieldAccessorParser, beanNamingExpert, modelType, modelDataBuilder, dynamicModelFactory)
		);
	}

}
