package com.mgs.mes.v3.mapper;

import com.mgs.config.reflection.ReflectionConfig;
import com.mgs.mes.v4.MapValueProcessor;
import com.mgs.mes.v4.MapWalker;

import java.util.ArrayList;
import java.util.List;

public class MapEntityConfig {
	private final ReflectionConfig reflectionConfig;

	public MapEntityConfig(ReflectionConfig reflectionConfig) {
		this.reflectionConfig = reflectionConfig;
	}

	public MapEntityContextFactory contextFactory (){
		return new MapEntityContextFactory(
				reflectionConfig.reflections(),
				reflectionConfig.fieldAccessorParser(),
				defaultManagers(),
				reflectionConfig.typeParser(),
				mapWalker(),
				mapValueProcessor());
	}

	private MapValueProcessor mapValueProcessor() {
		return new MapValueProcessor(
				reflectionConfig.reflections(),
				reflectionConfig.typeParser()
		);
	}

	private MapWalker mapWalker() {
		return new MapWalker(
				reflectionConfig.fieldAccessorParser(),
				reflectionConfig.beanNamingExpert(),
				reflectionConfig.reflections(),
				reflectionConfig.typeParser()
		);
	}

	private List<MapEntityManager> defaultManagers() {
		List<MapEntityManager> defaultManagers = new ArrayList<>();
		defaultManagers.add(new MethodDelegator(reflectionConfig.reflections()));
		return defaultManagers;
	}
}
