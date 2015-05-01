package com.mgs.mes.v3;

import com.mgs.config.reflection.ReflectionConfig;

import java.util.ArrayList;
import java.util.List;

public class MapEntityConfig {
	private final ReflectionConfig reflectionConfig;

	public MapEntityConfig(ReflectionConfig reflectionConfig) {
		this.reflectionConfig = reflectionConfig;
	}

	public MapEntityContextFactory contextFactory (){
		return new MapEntityContextFactory(reflectionConfig.reflections(), reflectionConfig.fieldAccessorParser(), reflectionConfig.beanNamingExpert(), defaultManagers());
	}

	private List<MapEntityManager> defaultManagers() {
		List<MapEntityManager> defaultManagers = new ArrayList<>();
		defaultManagers.add(new BasicEntityManager(reflectionConfig.reflections()));
		return defaultManagers;
	}
}