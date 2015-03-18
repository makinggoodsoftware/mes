package com.mgs.mes.model.builder;

import com.mgs.mes.model.data.EntityData;
import com.mgs.mes.model.data.EntityDataBuilder;
import com.mgs.mes.model.entity.Entity;
import com.mgs.mes.model.entity.EntityBuilder;
import com.mgs.mes.model.factory.EntityFactory;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.FieldAccessorParser;
import org.bson.types.ObjectId;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.mgs.reflection.FieldAccessorType.BUILDER;
import static java.util.Optional.empty;
import static java.util.Optional.of;

class EntityBuilderCallInterceptor<T extends Entity> implements InvocationHandler, EntityBuilder<T> {
	private final FieldAccessorParser fieldAccessorParser;
	private final BeanNamingExpert beanNamingExpert;
	private final Class<T> modelType;
	private final EntityDataBuilder entityDataBuilder;
	private final EntityFactory<EntityData> entityFactory;

	public EntityBuilderCallInterceptor(FieldAccessorParser fieldAccessorParser, BeanNamingExpert beanNamingExpert, Class<T> modelType, EntityDataBuilder entityDataBuilder, EntityFactory<EntityData> entityFactory) {
		this.fieldAccessorParser = fieldAccessorParser;
		this.beanNamingExpert = beanNamingExpert;
		this.modelType = modelType;
		this.entityDataBuilder = entityDataBuilder;
		this.entityFactory = entityFactory;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().equals("create")) {
			return create();
		}else if (method.getName().equals("withId")) {
			return withId((ObjectId) args[0]);
		}else{
			captureBuilderMethodCall(method, args[0]);
			return proxy;
		}
	}

	private void captureBuilderMethodCall(Method method, Object value) {
		FieldAccessor fieldAccessor = fieldAccessorParser.parse(method).orElseThrow(IllegalArgumentException::new);
		if (fieldAccessor.getType() != BUILDER) throw new IllegalArgumentException();

		updateField(fieldAccessor.getFieldName(), value);
	}

	@Override
	public EntityBuilder<T> withId(ObjectId id) {
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
		entityDataBuilder.with(fieldAccessor, value);
	}

	@Override
	public T create() {
		return entityFactory.from(modelType, entityDataBuilder.build());
	}
}
