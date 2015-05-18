package com.mgs.mes.services.core.builder;

import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.entity.data.EntityDataBuilder;
import com.mgs.mes.entity.factory.entity.EntityFactory;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.services.core.reference.EntityReferenceProvider;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.FieldAccessorParser;
import com.mgs.reflection.Reflections;
import com.mongodb.DBObject;
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
	private final Reflections reflections;
	private final EntityReferenceProvider entityReferenceProvider;

	public EntityBuilderCallInterceptor(FieldAccessorParser fieldAccessorParser, BeanNamingExpert beanNamingExpert, Class<T> modelType, EntityDataBuilder entityDataBuilder, EntityFactory<EntityData> entityFactory, Reflections reflections, EntityReferenceProvider entityReferenceProvider) {
		this.fieldAccessorParser = fieldAccessorParser;
		this.beanNamingExpert = beanNamingExpert;
		this.modelType = modelType;
		this.entityDataBuilder = entityDataBuilder;
		this.entityFactory = entityFactory;
		this.reflections = reflections;
		this.entityReferenceProvider = entityReferenceProvider;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().equals("create")) {
			return create();
		} else if (method.getName().equals("withId")) {
			return withId((ObjectId) args[0]);
		} else if (method.getName().equals("getEntityType")) {
			return getEntityType();
		} else if (method.getName().equals("asDbo")) {
			return asDbo();
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
	public Class<T> getEntityType() {
		return modelType;
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

	@Override
	public T create() {
		return entityFactory.from(modelType, entityDataBuilder.build());
	}

	@Override
	public DBObject asDbo() {
		return entityDataBuilder.build().getDbo();
	}

	private void updateField(String fieldName, Object value) {
		String getterName = beanNamingExpert.getGetterName(fieldName);
		FieldAccessor fieldAccessor = fieldAccessorParser.parse(modelType, getterName).orElseThrow(IllegalArgumentException::new);
//		if (reflections.isAssignableTo(fieldAccessor.getDeclaredType(), OneToOne.class)){
//			Entity casted = (Entity) value;
//			entityDataBuilder.with(fieldAccessor, entityReferenceProvider.newReference(casted));
//		} else if (reflections.isAssignableTo(fieldAccessor.getDeclaredType(), OneToMany.class)){
//			@SuppressWarnings("unchecked") List<? extends Entity> casted = (List<? extends Entity>) value;
//			entityDataBuilder.with(fieldAccessor, entityReferenceProvider.newReferences(casted));
//		}else {
//			entityDataBuilder.with(fieldAccessor, value);
//		}
	}

}
