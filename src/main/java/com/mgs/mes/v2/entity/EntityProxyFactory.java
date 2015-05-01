package com.mgs.mes.v2.entity;

import com.mgs.mes.entity.data.EntityData;
import com.mgs.mes.entity.factory.entity.entityData.EntityCallInterceptor;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.OneToMany;
import com.mgs.mes.model.OneToOne;
import com.mgs.mes.v2.entity.method.EntityMethodInterceptor;
import com.mgs.mes.v2.entity.property.manager.OneToManyManager;
import com.mgs.mes.v2.entity.property.manager.OneToOneManager;
import com.mgs.reflection.Reflections;
import com.mongodb.DBObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.reflect.Proxy.newProxyInstance;

public class EntityProxyFactory {
	private final OneToOneManager oneToOneManager;
	private final OneToManyManager oneToManyManager;
	private final Reflections reflections;

	public EntityProxyFactory(OneToOneManager oneToOneManager, OneToManyManager oneToManyManager, Reflections reflections) {
		this.oneToOneManager = oneToOneManager;
		this.oneToManyManager = oneToManyManager;
		this.reflections = reflections;
	}

	public <T extends Entity>
	T from(
			Class<T> entityType,
			Optional<Class<? extends Entity>> wrappedEntityType,
			DBObject dbObject,
			Map<String, Object> domainValues
	){

		Map<String, EntityMethodInterceptor> interceptors = createInterceptors(entityType);
		//noinspection unchecked
		return (T) newProxyInstance(
				EntityFactory.class.getClassLoader(),
				new Class[]{entityType},
				new EntityCallInterceptor(
						entityType,
						wrappedEntityType,
						new EntityData(
							dbObject,
							domainValues
						),
						interceptors
				)
		);
	}

	private Map<String, EntityMethodInterceptor> createInterceptors(Class<? extends Entity> entityType) {
		HashMap<String, EntityMethodInterceptor> result = new HashMap<>();

		if (reflections.isAssignableTo(entityType, OneToOne.class)){
			result.put("retrieve", (fromEntity, wrappedEntityType) -> oneToOneManager.onRetrieve((OneToOne) fromEntity));
		} else if (reflections.isAssignableTo(entityType, OneToMany.class)) {
			//noinspection unchecked
			result.put("retrieveAll", (fromEntity, wrappedEntityType) -> oneToManyManager.onRetrieveAll((OneToMany) fromEntity));
		}

		return result;
	}

}
