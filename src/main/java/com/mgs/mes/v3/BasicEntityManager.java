package com.mgs.mes.v3;


import com.mgs.reflection.Reflections;

import java.lang.reflect.Method;
import java.util.Optional;

import static java.util.Optional.empty;

class BasicEntityManager implements MapEntityManager<MapEntity>{
	private final Reflections reflections;

	BasicEntityManager(Reflections reflections) {
		this.reflections = reflections;
	}

	@Override
	public Class<MapEntity> getSupportedType() {
		return MapEntity.class;
	}

	@Override
	public Optional<EntityMethod<MapEntity>> applies(Method method) {
		switch (method.getName()) {
			case "toString":
				return Optional.of(onToString());
			case "equals":
				return Optional.of(onEquals());
			case "asMap":
				return Optional.of(onAsMap());
			case "hashCode":
				return Optional.of(onHashCode());
		}

		return empty();
	}

	private EntityMethod<MapEntity> onToString() {
		return (type, value, map, params) -> toString(value);
	}

	private EntityMethod<MapEntity> onEquals() {
		return (type, value, map, params) -> equals(type, value, params[0]);
	}

	private EntityMethod<MapEntity> onHashCode() {
		return (type, value, map, params) -> hashCode(value);
	}


	private EntityMethod<MapEntity> onAsMap() {
		return (type, value, map, params) -> map;
	}

	private Object toString(MapEntity value) {
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
