package com.mgs.mes.v3.mapper;

import com.mgs.mes.v4.typeParser.TypeParser;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mgs.reflection.Reflections;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class MapEntityContextFactory {
	private final Reflections reflections;
	private final FieldAccessorParser fieldAccessorParser;
	private final BeanNamingExpert beanNamingExpert;
	private final List<MapEntityManager> defaultManagers;
	private final TypeParser typeParser;

	public MapEntityContextFactory(Reflections reflections, FieldAccessorParser fieldAccessorParser, BeanNamingExpert beanNamingExpert, List<MapEntityManager> defaultManagers, TypeParser typeParser) {
		this.reflections = reflections;
		this.fieldAccessorParser = fieldAccessorParser;
		this.beanNamingExpert = beanNamingExpert;
		this.defaultManagers = defaultManagers;
		this.typeParser = typeParser;
	}

	public MapEntityContext defaultContext(){
		ManagerLocator managerLocator = new ManagerLocator(reflections, defaultManagers);
		return new MapEntityContext(managerLocator, fieldAccessorParser, beanNamingExpert, reflections, new TypeParser(), mapBuilder());
	}

	public MapEntityContext withManagers(MapEntityManager... managers){
		List<MapEntityManager> allManagers = new ArrayList<>();
		allManagers.addAll(defaultManagers);
		allManagers.addAll(asList(managers));
		ManagerLocator managerLocator = new ManagerLocator(reflections, allManagers);
		return new MapEntityContext(managerLocator, fieldAccessorParser, beanNamingExpert, reflections, new TypeParser(), mapBuilder());
	}

	private MapEntityFactory mapBuilder() {
		return new MapEntityFactory(typeParser, fieldAccessorParser);
	}
}
