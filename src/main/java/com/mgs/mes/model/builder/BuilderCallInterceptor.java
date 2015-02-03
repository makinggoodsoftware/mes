package com.mgs.mes.model.builder;

import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.model.MongoEntityBuilder;
import com.mgs.mes.model.data.ModelData;
import com.mgs.mes.model.data.ModelDataBuilder;
import com.mgs.mes.model.factory.ModelFactory;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.FieldAccessorParser;
import org.bson.types.ObjectId;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.mgs.reflection.FieldAccessorType.BUILDER;
import static java.util.Optional.empty;
import static java.util.Optional.of;

class BuilderCallInterceptor<T extends MongoEntity> implements InvocationHandler, MongoEntityBuilder<T> {
	private final FieldAccessorParser fieldAccessorParser;
	private final BeanNamingExpert beanNamingExpert;
	private final Class<T> modelType;
	private final ModelDataBuilder modelDataBuilder;
	private final ModelFactory<ModelData> modelFactory;

	public BuilderCallInterceptor(FieldAccessorParser fieldAccessorParser, BeanNamingExpert beanNamingExpert, Class<T> modelType, ModelDataBuilder modelDataBuilder, ModelFactory<ModelData> modelFactory) {
		this.fieldAccessorParser = fieldAccessorParser;
		this.beanNamingExpert = beanNamingExpert;
		this.modelType = modelType;
		this.modelDataBuilder = modelDataBuilder;
		this.modelFactory = modelFactory;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().equals("create")) {
			return create();
		}else if (method.getName().equals("withId")) {
			return withId((ObjectId) args[0]);
		}else{
			captureUpdateMethodCall(method, args[0]);
			return proxy;
		}
	}

	private void captureUpdateMethodCall(Method method, Object value) {
		FieldAccessor fieldAccessor = fieldAccessorParser.parse(method).orElseThrow(IllegalArgumentException::new);
		if (fieldAccessor.getType() != BUILDER) throw new IllegalArgumentException();

		updateField(fieldAccessor.getFieldName(), value);
	}

	@Override
	public MongoEntityBuilder<T> withId(ObjectId id) {
		if (id == null) {
			updateField("id", empty());
		}else{
			updateField("id", of(id));
		}
		return this;
	}

	private void updateField(String fieldName, Object value) {
		String getterName = beanNamingExpert.getGetterName(fieldName);
		FieldAccessor fieldAccessor = fieldAccessorParser.parse(modelType, getterName).orElseThrow(IllegalArgumentException::new);
		modelDataBuilder.with(fieldAccessor, value);
	}

	@Override
	public T create() {
		return modelFactory.from(modelType, modelDataBuilder.build());
	}
}
