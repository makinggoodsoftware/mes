package com.mgs.mes.entity.factory.entity.entityData;

import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.model.Entity;
import com.mgs.mes.v2.entity.method.EntityMethodInterceptor;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityCallInterceptor<T extends Entity> implements InvocationHandler, Entity {
	@SuppressWarnings({"unused", "FieldCanBeLocal"})
	private final Class<T> thisEntityType;
	private final Optional<Class<? extends Entity>> wrappedEntityType;
	private final EntityData entityData;
	private final Map<String, EntityMethodInterceptor> methodInterceptors;

	public EntityCallInterceptor(Class<T> thisEntityType, Optional<Class<? extends Entity>> wrappedEntityType, EntityData entityData, Map<String, EntityMethodInterceptor> methodInterceptors) {
		this.thisEntityType = thisEntityType;
		this.wrappedEntityType = wrappedEntityType;
		this.entityData = entityData;
		this.methodInterceptors = methodInterceptors;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		switch (method.getName()) {
			case "asDbo":
				return asDbo();
			case "equals":
				Entity equalsRight = (Entity) args[0];
				return equals(equalsRight);
			case "toString":
				return toString();
			case "dataEquals":
				return dataEquals((Entity) args[0]);
			default:
				if (args.length > 0) throw new IllegalArgumentException();
				//noinspection unchecked
				EntityMethodInterceptor<T> entityMethodInterceptor = methodInterceptors.get(method.getName());
				if (entityMethodInterceptor != null) {
					//noinspection unchecked
					return entityMethodInterceptor.apply((T) this, wrappedEntityType);
				}
				return entityData.get(method.getName());
		}
	}

	@Override
	public DBObject asDbo() {
		return entityData.getDbo();
	}

	@Override
	public Optional<ObjectId> getId() {
		//noinspection unchecked
		return (Optional<ObjectId>) entityData.get("getId");
	}

	@Override
	public boolean dataEquals(Entity entity) {
		Map<String, Object> thisValues = extractDataValues(this.asDbo());
		Map<String, Object> otherValues = extractDataValues(entity.asDbo());
		return thisValues.equals(otherValues);
	}

	private Map<String, Object> extractDataValues(DBObject thisDbo) {
		//noinspection unchecked
		Stream<Map.Entry<String, Object>> stream = thisDbo.toMap().entrySet().stream();
		return stream.
				filter(
						(dboEntry) -> !dboEntry.getKey().equals("_id")
				).
				collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue
				));
	}

	@Override
	public String toString() {
		return entityData.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		EntityCallInterceptor that = null;
		if (EntityCallInterceptor.class.isAssignableFrom(o.getClass())){
			that = (EntityCallInterceptor) o;
		}else if (Entity.class.isAssignableFrom(o.getClass())) {
			if (o instanceof Proxy){
				that = (EntityCallInterceptor) Proxy.getInvocationHandler(o);
			}
		}

		return that != null && entityData.equals(that.entityData);

	}

	@Override
	public int hashCode() {
		return entityData.hashCode();
	}
}