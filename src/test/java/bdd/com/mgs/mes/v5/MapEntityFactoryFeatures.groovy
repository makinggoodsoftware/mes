package com.mgs.mes.v5

import com.mgs.config.reflection.ReflectionConfig
import com.mgs.mes.v3.mapper.MapEntity
import spock.lang.Specification


class MapEntityFactoryFeatures extends Specification{
    MapEntityFactoryConfig mapEntityFactoryConfig = new MapEntityFactoryConfig(new ReflectionConfig())
    MapEntityFactory objectFactory

    def "setup" (){
        objectFactory = mapEntityFactoryConfig.mapEntityFactory([])
    }

    def "should create object from simple interface" (){
        when:
        SimpleInterface result = objectFactory.immutable(SimpleInterface, [value:"hi"])

        then:
        result.value == "string"
    }

    static interface SimpleInterface extends MapEntity{
        String getValue()
    }
}
