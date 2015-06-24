package com.mgs.mes.v5;

import com.mgs.config.reflection.ReflectionConfig;
import com.mgs.mes.v4.MapEntityFieldTransformer;
import com.mgs.mes.v4.MapWalker;

import java.util.List;

public class MapEntityFactoryConfig {
    private final ReflectionConfig reflectionConfig;

    public MapEntityFactoryConfig(ReflectionConfig reflectionConfig) {
        this.reflectionConfig = reflectionConfig;
    }

    public MapEntityFactory mapEntityFactory (List<Object> managers){
        return new MapEntityFactory(
                reflectionConfig.typeParser(),
                mapEntityMethodLinker(),
                managers,
                mapEntityTransformer(),
                mapManager()
        );
    }

    private MapManager mapManager() {
        return new MapManager();
    }

    private MapEntityTransformer mapEntityTransformer() {
        return new MapEntityTransformer(mapWalker(), mapEntityFieldTransformer());
    }

    private MapEntityFieldTransformer mapEntityFieldTransformer() {
        return new MapEntityFieldTransformer(
                reflectionConfig.reflections(),
                reflectionConfig.typeParser()
        );
    }

    private MapWalker mapWalker (){
        return new MapWalker(
                reflectionConfig.fieldAccessorParser(),
                reflectionConfig.beanNamingExpert(),
                reflectionConfig.reflections(),
                reflectionConfig.typeParser()
        );
    }

    private MapEntityMethodLinker mapEntityMethodLinker() {
        return new MapEntityMethodLinker(typeParser);
    }
}
