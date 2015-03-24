package com.mgs.mes.build.data
import com.mgs.mes.build.data.transformer.DboTransformer
import com.mgs.mes.build.data.transformer.EntityDataTransformer
import com.mgs.mes.build.data.transformer.FieldAccessorMapTransformer
import com.mgs.mes.build.factory.entity.EntityFactory
import com.mgs.mes.model.Entity
import com.mgs.reflection.BeanNamingExpert
import com.mgs.reflection.FieldAccessor
import com.mgs.reflection.FieldAccessorParser
import com.mongodb.DBObject
import spock.lang.Specification

import static com.mgs.reflection.FieldAccessorType.GET
import static java.util.Optional.empty
import static java.util.Optional.of

@SuppressWarnings("GroovyAssignabilityCheck")
class EntityDataBuilderFactorySpecification extends Specification {
    EntityDataBuilderFactory testObj
    FieldAccessorParser fieldAccessorParserMock = Mock ()
    BeanNamingExpert beanNamingExpertMock = Mock ()

    def setup (){
        EntityFactory<EntityData> entityFactoryMock = Mock ()


        EntityDataTransformer<DBObject> dboEntityDataTransformer = new DboTransformer(
                entityFactoryMock,
                beanNamingExpertMock,
                fieldAccessorParserMock
        )

        EntityDataTransformer<Map<FieldAccessor, Object>> mapEntityDataTransformer = new FieldAccessorMapTransformer ()
        testObj = new EntityDataBuilderFactory(
                new EntityDataFactory(dboEntityDataTransformer, mapEntityDataTransformer),
                beanNamingExpertMock,
                fieldAccessorParserMock
        )

        FieldAccessor accessor = new FieldAccessor(String, "getString", "string", "get", GET)
        fieldAccessorParserMock.parse(EntityTest) >> [accessor].stream()
        beanNamingExpertMock.getGetterName("string") >> "getString"
        beanNamingExpertMock.getGetterName("invalidField") >> "getInvalidField"
        fieldAccessorParserMock.parse(EntityTest, "getString")   >> of(accessor)
        fieldAccessorParserMock.parse(EntityTest, "getInvalidField")   >> empty()
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
        EntityTest baseLine = Mock ()
        baseLine.string >> "baseline value"

        when:
        def result = testObj.from(EntityTest, baseLine)

        then:
        result.build().get("getString") == "baseline value"
    }

    public static interface EntityTest extends Entity {
        public String getString()
    }
}
