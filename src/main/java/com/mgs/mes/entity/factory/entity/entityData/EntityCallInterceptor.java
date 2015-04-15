package com.mgs.mes.entity.factory.entity.entityData;

import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.model.Entity;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityCallInterceptor implements InvocationHandler, Entity {
	protected final EntityData entityData;

	public EntityCallInterceptor(EntityData entityData) {
		this.entityData = entityData;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().equals("asDbo")) {
			return asDbo();
		} else if (method.getName().equals("equals")) {
			Entity equalsRight = (Entity) args[0];
			return equals(equalsRight);
		} else if (method.getName().equals("toString")) {
			return toString();
		} else if (method.getName().equals("dataEquals")) {
			return dataEquals((Entity) args[0]);
		} else {
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