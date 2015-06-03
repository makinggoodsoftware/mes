package com.mgs.mes.v3.mapper;

import com.mgs.reflection.Reflections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManagerLocator {
	private final Reflections reflections;
	private final List<MapEntityManager> managers;

	public ManagerLocator(Reflections reflections, List<MapEntityManager> managers) {
		this.reflections = reflections;
		this.managers = managers;
	}

	public <T extends MapEntity> List<MapEntityManager<T>> byType(Class<T> type) {
		List<MapEntityManager<T>> entityManagers = new ArrayList<>();
		for (MapEntityManager manager : managers) {
			if (reflections.isAssignableTo(type, manager.getSupportedType())){
				entityManagers.add(manager);
			}
		}
		if (entityManagers.size() == 0){
			throw new IllegalArgumentException("Can't find a manager for the type: " + type);
		}
		Collections.sort(entityManagers, (left, right) -> right.getInheritanceLevel().compareTo(left.getInheritanceLevel()));
		return entityManagers;
	}
}
