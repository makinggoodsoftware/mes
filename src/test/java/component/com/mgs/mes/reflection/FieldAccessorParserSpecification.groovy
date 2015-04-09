package com.mgs.mes.reflection
import com.mgs.config.reflection.ReflectionConfig
import com.mgs.mes.simpleModel.entityA.EntityA
import com.mgs.mes.simpleModel.entityA.EntityABuilder
import com.mgs.reflection.FieldAccessor
import com.mgs.reflection.FieldAccessorParser
import spock.lang.Specification

import static com.mgs.reflection.FieldAccessorType.BUILDER
import static com.mgs.reflection.FieldAccessorType.GET
import static java.util.stream.Collectors.toList

class FieldAccessorParserSpecification extends Specification {
    FieldAccessorParser testObj
    ReflectionConfig reflectionConfig = new ReflectionConfig()

    def "setup" (){
        testObj = reflectionConfig.fieldAccessorParser()
    }

    def "should parse get method" () {
        when:
        def result = testObj.parse(EntityA.getMethod("getEntityAfield1")).get()

        then:
        result.fieldName == "entityAfield1"
        result.type == GET
        result.prefix == "get"
        result.declaredType == String
    }

    def "should parse with method" () {
        when:
        def result = testObj.parse(EntityABuilder.getMethod("withEntityAfield1", String)).get()

        then:
        result.fieldName == "entityAfield1"
        result.type == BUILDER
        result.prefix == "with"
        result.declaredType == String
    }

    def "should fail parsing method" (){
        when:
        def result = testObj.parse(method)

        then:
        result.isPresent() == present

        where:
        method                                                  | present
        BadGetters.getMethod("getSomething")                    | false
        BadGetters.getMethod("getWithParam", String)            | false
        BadGetters.getMethod("getWithParamAndReturn", String)   | false
        BadGetters.getMethod("pointless")                       | false
        BadGetters.getMethod("withNoReturningSelf", String)     | false
        BadGetters.getMethod("withBadParams")                   | false
        BadGetters.getMethod("withBadParams", String, String)   | false
    }

    def "should parse entire mongo entity child class" (){
        when:
        def result = testObj.parse(Entity).collect(toList())

        then:
        result.size() == 2
        result.get(0).fieldName == "id"
        result.get(1).fieldName == "field1"
    }

    def "should parse all correctly" (){
        when:
        def result = testObj.parseAll(Entity)

        then:
        result.entrySet().size() == 3
        result.get(Entity.getMethod("getField1")).get() == new FieldAccessor(
                String,
                "getField1",
                "field1",
                "get",
                GET
        )
        result.get(Entity.getMethod("getId")).get() == new FieldAccessor(
                Optional,
                "getId",
                "id",
                "get",
                GET
        )
        ! result.get(Entity.getMethod("asDbo")).isPresent()
    }

    static interface Entity extends com.mgs.mes.model.Entity{
        public String getField1();
    }

    static interface BadGetters {
        public void getSomething ()
        public void getWithParam (String blah)
        public String getWithParamAndReturn (String blah)
        public String pointless ()
        public String withNoReturningSelf (String blah)
        public BadGetters withBadParams ()
        public BadGetters withBadParams (String a, String b)
    }
}
