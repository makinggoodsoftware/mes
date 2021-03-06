package com.mgs.mes.v3.mapper;

import com.mgs.mes.v4.MapEntityFieldTransformer;
import com.mgs.mes.v4.MapWalker;
import com.mgs.mes.v4.typeParser.TypeParser;
import com.mgs.reflection.FieldAccessorParser;
import com.mgs.reflection.Reflections;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class MapEntityContextFactory {
	private final Reflections reflections;
	private final FieldAccessorParser fieldAccessorParser;
	private final List<MapEntityManager> defaultManagers;
	private final TypeParser typeParser;
	private final MapWalker mapWalker;
	private final MapEntityFieldTransformer mapEntityFieldTransformer;

	public MapEntityContextFactory(Reflections reflections, FieldAccessorParser fieldAccessorParser, List<MapEntityManager> defaultManagers, TypeParser typeParser, MapWalker mapWalker, MapEntityFieldTransformer mapEntityFieldTransformer) {
		this.reflections = reflections;
		this.fieldAccessorParser = fieldAccessorParser;
		this.defaultManagers = defaultManagers;
		this.typeParser = typeParser;
		this.mapWalker = mapWalker;
		this.mapEntityFieldTransformer = mapEntityFieldTransformer;
	}

	public MapEntityContext defaultContext(){
		ManagerLocator managerLocator = new ManagerLocator(reflections, defaultManagers);
		return new MapEntityContext(managerLocator, new TypeParser(), mapBuilder(), mapWalker, mapEntityFieldTransformer);
	}

	public MapEntityContext withManagers(MapEntityManager... managers){
		List<MapEntityManager> allManagers = new ArrayList<>();
		allManagers.addAll(defaultManagers);
		allManagers.addAll(asList(managers));
		ManagerLocator managerLocator = new ManagerLocator(reflections, allManagers);
		return new MapEntityContext(managerLocator, new TypeParser(), mapBuilder(), mapWalker, mapEntityFieldTransformer);
	}

	private MapEntityFactory mapBuilder() {
		return new MapEntityFactory(typeParser, fieldAccessorParser, mapWalker, mapEntityFieldTransformer);
	}
}
