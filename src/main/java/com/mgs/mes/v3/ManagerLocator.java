package com.mgs.mes.v3;

import com.mgs.reflection.Reflections;

import java.util.List;

public class ManagerLocator {
	private final Reflections reflections;
	private final List<MapEntityManager> managers;

	public ManagerLocator(Reflections reflections, List<MapEntityManager> managers) {
		this.reflections = reflections;
		this.managers = managers;
	}

	public <T extends MapEntity> MapEntityManager<T> byType(Class<T> type) {
		for (MapEntityManager manager : managers) {
			if (reflections.isAssignableTo(type, manager.getSupportedType())){
				//noinspection unchecked
				return manager;
			}
		}
		throw new IllegalArgumentException("Can't find a manager for the type: " + type);
	}
}
