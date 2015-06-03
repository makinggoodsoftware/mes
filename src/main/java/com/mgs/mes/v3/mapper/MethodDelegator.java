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
				return (type, value, map, params) -> toString(value);
			case "equals":
				return (type, value, map, params) -> equals(type, value, params[0]);
			case "asMap":
				return (type, value, map, params) -> map;
			case "hashCode":
				return (type, value, map, params) -> hashCode(value);
		}

		return null;
	}

	public String toString(MapEntity value) {
		return value.asMap().toString();
	}

	public int hashCode(MapEntity mapEntity) {
		return mapEntity.asMap().hashCode();
	}

	public boolean equals(Class<? extends MapEntity> type, MapEntity thisMapEntity, Object thatMapEntity) {
		if (thisMapEntity == null && thatMapEntity == null) return true;
		if (thatMapEntity == null) return false;
		if (thisMapEntity == null) return false;
		if (!reflections.isAssignableTo(thatMapEntity.getClass(), type)) return false;

		MapEntity casted = (MapEntity) thatMapEntity;
		return thisMapEntity.asMap().equals(casted.asMap());
	}

}
