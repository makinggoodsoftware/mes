package com.mgs.mes.v3.mapper;


import com.mgs.reflection.Reflections;

import java.lang.reflect.Method;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

class MethodDelegator implements MapEntityManager<MapEntity>{
	private final Reflections reflections;

	MethodDelegator(Reflections reflections) {
		this.reflections = reflections;
	}

	@Override
	public Integer getInheritanceLevel() {
		return 0;
	}

	@Override
	public Class<MapEntity> getSupportedType() {
		return MapEntity.class;
	}

	@Override
	public Optional<EntityMethod<MapEntity>> applies(Method method) {
		EntityMethod<MapEntity> mapEntityEntityMethod = rawApplies(method);
		return mapEntityEntityMethod == null ? empty() : of(mapEntityEntityMethod);
	}

	private EntityMethod<MapEntity> rawApplies(Method method) {
		switch (method.getName()) {
			case "toString":
				return (value, interceptor, params) -> toString(value);
			case "equals":
				//noinspection unchecked
				return (value, interceptor, params) -> equals(interceptor.getType().getActualType().get(), value, params[0]);
			case "getDomainMap":
				return (value, interceptor, params) -> interceptor.getDomainMap();
			case "getValueMap":
				return (value, interceptor, params) -> {
					if (interceptor.isModifiable()) throw new IllegalStateException("Can't access the value map of a Map Entity while building it.");
					return interceptor.getValueMap();
				};
			case "hashCode":
				return (value, interceptor, params) -> hashCode(value);
		}

		return null;
	}

	public String toString(MapEntity value) {
		return value.getDomainMap().toString();
	}

	public int hashCode(MapEntity mapEntity) {
		return mapEntity.getDomainMap().hashCode();
	}

	public boolean equals(Class<? extends MapEntity> type, MapEntity thisMapEntity, Object thatMapEntity) {
		if (thisMapEntity == null && thatMapEntity == null) return true;
		if (thatMapEntity == null) return false;
		if (thisMapEntity == null) return false;
		if (!reflections.isAssignableTo(thatMapEntity.getClass(), type)) return false;

		MapEntity casted = (MapEntity) thatMapEntity;
		return thisMapEntity.getDomainMap().equals(casted.getDomainMap());
	}

}
