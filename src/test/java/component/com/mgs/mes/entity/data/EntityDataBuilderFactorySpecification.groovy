package com.mgs.mes.entity.data

import com.mgs.config.mes.entity.EntityConfig
import com.mgs.config.reflection.ReflectionConfig
import com.mgs.mes.model.Entity
import spock.lang.Specification

import static java.util.Optional.empty

class EntityDataBuilderFactorySpecification extends Specification {
    EntityDataBuilderFactory testObj
    ReflectionConfig reflectionConfig = new ReflectionConfig()
    EntityConfig buildConfig = new EntityConfig(reflectionConfig)

    def setup (){
        testObj = buildConfig.entityData().builderFactory();
    }

    def "should create an empty builder" (){
        when:
        def result = testObj.empty(EntityTest)
        result.with("string", "test")

        then:
        result.build().get("getString") == "test"

        when:
        result.with("invalidField", "blah")

        then:
        thrown(IllegalArgumentException)
    }

    def "should create a builder from a different entity" (){
        given:
        EntityTest baseLine = Mock (EntityTest)
        baseLine.string >> "baseline value"
        baseLine.id >> empty()

        when:
        def result = testObj.from(EntityTest, baseLine)

        then:
        result.build().get("getString") == "baseline value"
    }

    public static interface EntityTest extends Entity {
        public String getString()
    }
}
