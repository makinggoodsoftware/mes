package com.mgs.mes.orm.modelBuilder;

import com.mgs.mes.model.ModelBuilder;
import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.orm.modelData.ModelDataBuilder;
import com.mgs.mes.orm.modelFactory.DynamicModelFactory;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.FieldAccessorParser;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.mgs.reflection.FieldAccessorType.BUILDER;

class BuilderCallInterceptor<T extends MongoEntity> implements InvocationHandler, ModelBuilder<T> {
	private final FieldAccessorParser fieldAccessorParser;
	private final BeanNamingExpert beanNamingExpert;
	private final Class<T> modelType;
	private final ModelDataBuilder modelDataBuilder;
	private final DynamicModelFactory dynamicModelFactory;

	public BuilderCallInterceptor(FieldAccessorParser fieldAccessorParser, BeanNamingExpert beanNamingExpert, Class<T> modelType, ModelDataBuilder modelDataBuilder, DynamicModelFactory dynamicModelFactory) {
		this.fieldAccessorParser = fieldAccessorParser;
		this.beanNamingExpert = beanNamingExpert;
		this.modelType = modelType;
		this.modelDataBuilder = modelDataBuilder;
		this.dynamicModelFactory = dynamicModelFactory;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().equals("create")) {
			return create();
		}else{
			captureUpdateMethodCall(method, args[0]);
			return proxy;
		}
	}

	private void captureUpdateMethodCall(Method method, Object value) {
		FieldAccessor fieldAccessor = fieldAccessorParser.parse(method).orElseThrow(IllegalArgumentException::new);
		if (fieldAccessor.getType() != BUILDER) throw new IllegalArgumentException();

		String getterName = beanNamingExpert.getGetterName(fieldAccessor.getFieldName());
		fieldAccessor = fieldAccessorParser.parse(modelType, getterName).orElseThrow(IllegalArgumentException::new);
		modelDataBuilder.with(fieldAccessor, value);
	}

	@Override
	public T create() {
		return dynamicModelFactory.dynamicModel(modelType, modelDataBuilder.build());
	}
}
