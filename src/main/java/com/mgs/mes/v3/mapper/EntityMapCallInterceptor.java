package com.mgs.mes.v3.mapper;

import com.mgs.mes.v4.typeParser.Declaration;
import com.mgs.mes.v4.typeParser.ParsedType;
import com.mgs.reflection.FieldAccessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class EntityMapCallInterceptor<T extends MapEntity> implements InvocationHandler {
	private final MapEntityFactory mapEntityFactory;
	private final ParsedType type;
	private final Map<String, Object> domainMap;
	private final Map<String, Object> valueMap;
	private final List<MapEntityManager<T>> entityManagers;
	private final Map<String, FieldAccessor> fieldAccessors;
	private final boolean modifiable;
	private final BiFunction<Declaration, EntityMapBuilder, Object> creator;

	public EntityMapCallInterceptor(MapEntityFactory mapEntityFactory, ParsedType type, Map<String, Object> domainMap, Map<String, Object> valueMap, List<MapEntityManager<T>> entityManager, Map<String, FieldAccessor> fieldAccessors, boolean modifiable, BiFunction<Declaration, EntityMapBuilder, Object> creator) {
		this.mapEntityFactory = mapEntityFactory;
		this.type = type;
		this.domainMap = domainMap;
		this.valueMap = valueMap;
		this.entityManagers = entityManager;
		this.fieldAccessors = fieldAccessors;
		this.modifiable = modifiable;
		this.creator = creator;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		for (MapEntityManager<T> entityManager : entityManagers) {
			Optional<EntityMethod<T>> entityMethod = entityManager.applies(method);
			if (entityMethod.isPresent()){
				//noinspection unchecked
				return entityMethod.get().execute((T) proxy, this, args);
			}
		}
		FieldAccessor accessor = fieldAccessors.get(method.getName());
		if (accessor == null) throw new IllegalStateException("Can't find the field accessor for the method: " + method.getName());
		return methodAccessorCall(proxy, accessor, args);
	}

	private Object methodAccessorCall(Object proxy, FieldAccessor accessor, Object[] args) {
		switch (accessor.getType()) {
			case GET:
				return domainMap.get(accessor.getFieldName());

			case BUILDER:
				return builderCall(proxy, accessor, args);

			default:
				throw new IllegalStateException();
		}
	}

	private Object builderCall(Object proxy, FieldAccessor accessor, Object[] args) {
		if (EntityMapBuilder.class.isAssignableFrom(args[0].getClass())){
			EntityMapBuilder entityMapBuilder = (EntityMapBuilder) args[0];
			Declaration builderType = accessor.getParameters().get(0).getOwnDeclaration().getParameters().get("T");
			return update(proxy, accessor.getFieldName(), creator.apply (builderType, entityMapBuilder));
		} else {
			return update(proxy, accessor.getFieldName(), args[0]);
		}
	}

	private Object update(Object proxy, String fieldName, Object value) {
		if (modifiable){
			domainMap.put(fieldName, value);
			return proxy;
		}else{
			MapEntity copyFrom = (MapEntity) proxy;
			Map<String, Object> domainMapCopy = new HashMap<>(copyFrom.asDomainMap());
			domainMapCopy.put(fieldName, value);
			return mapEntityFactory.fromMap(
					type,
					fieldAccessors,
					entityManagers,
					domainMapCopy
			);
		}
	}

	public Map<String, Object> getDomainMap() {
		return domainMap;
	}

	public List<MapEntityManager<T>> getEntityManagers() {
		return entityManagers;
	}

	public Map<String, FieldAccessor> getFieldAccessors() {
		return fieldAccessors;
	}

	public MapEntityFactory getMapEntityFactory() {
		return mapEntityFactory;
	}

	public boolean isModifiable() {
		return modifiable;
	}

	public ParsedType getType() {
		return type;
	}

	public Map<String, Object> getValueMap() {
		return valueMap;
	}
}
