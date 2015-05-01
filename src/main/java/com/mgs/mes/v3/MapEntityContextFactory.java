package com.mgs.mes.v3;

import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mgs.reflection.Reflections;

import java.util.List;

public class MapEntityContextFactory {
	private final Reflections reflections;
	private final FieldAccessorParser fieldAccessorParser;
	private final BeanNamingExpert beanNamingExpert;
	private final List<MapEntityManager> defaultManagers;

	public MapEntityContextFactory(Reflections reflections, FieldAccessorParser fieldAccessorParser, BeanNamingExpert beanNamingExpert, List<MapEntityManager> defaultManagers) {
		this.reflections = reflections;
		this.fieldAccessorParser = fieldAccessorParser;
		this.beanNamingExpert = beanNamingExpert;
		this.defaultManagers = defaultManagers;
	}

	public MapEntityContext defaultContext(){
		ManagerLocator managerLocator = new ManagerLocator(reflections, defaultManagers);
		return new MapEntityContext(managerLocator, fieldAccessorParser, beanNamingExpert, reflections);
	};
}
