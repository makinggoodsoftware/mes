package com.mgs.mes.v5

import spock.lang.Specification

import java.lang.reflect.Method

class MapEntityMethodLinkerSpecification extends Specification {
    MapEntityMethodLinker mapEntityMethodLinker = new MapEntityMethodLinker(typeParser)

    def "should link the methods correctly" (){
        given:
        SimpleGetterManager simpleGetterManager = Mock()

        when:
        Map<Method, MapEntityMethodManager> result = mapEntityMethodLinker.link(SimpleGetter, [simpleGetterManager])

        then:
        result.size() == 1
    }

    interface SimpleGetter {
        String getMe ()
    }

    interface SimpleGetterManager {
        @MapEntityMethod (pattern = "get{fieldName}")
        public onGetter (@PatternReader(extract ="fieldName")String fieldName);
    }
}
