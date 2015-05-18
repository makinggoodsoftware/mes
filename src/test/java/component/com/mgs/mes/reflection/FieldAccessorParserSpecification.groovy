package com.mgs.mes.reflection
import com.mgs.config.reflection.ReflectionConfig
import com.mgs.mes.model.Entity
import com.mgs.mes.simpleModel.entityA.EntityA
import com.mgs.mes.simpleModel.entityA.EntityABuilder
import com.mgs.reflection.FieldAccessor
import com.mgs.reflection.FieldAccessorParser
import com.mgs.reflection.GenericType
import spock.lang.Specification

import static com.mgs.reflection.FieldAccessorType.BUILDER
import static com.mgs.reflection.FieldAccessorType.GET
import static java.util.Optional.empty
import static java.util.Optional.of
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
        result.parsedTypes == []
    }

    def "should parse get method with parametrized type" (){
        when:
        def result = testObj.parse(SimpleEntity.getMethod("getWithGenerics")).get()

        then:
        result.fieldName == "withGenerics"
        result.type == GET
        result.prefix == "get"
        result.declaredType == Map
        result.parsedTypes == [
                new GenericType(String.name, of(String), getChildrenParametrizedTypes),
                new GenericType(SimpleEntity.name, of(SimpleEntity), getChildrenParametrizedTypes)
        ]
    }

    def "should parse with method" () {
        when:
        def result = testObj.parse(EntityABuilder.getMethod("withEntityAfield1", String)).get()

        then:
        result.fieldName == "entityAfield1"
        result.type == BUILDER
        result.prefix == "with"
        result.declaredType == String
        result.parsedTypes == []
    }

    def "should parse with method and generics" () {
        when:
        def result = testObj.parse(SimpleEntity.getMethod("withGenerics", Map)).get()

        then:
        result.fieldName == "generics"
        result.type == BUILDER
        result.prefix == "with"
        result.declaredType == Map
        result.parsedTypes == [
                new GenericType(String.name, of(String), getChildrenParametrizedTypes),
                new GenericType(SimpleEntity.name, of(SimpleEntity), getChildrenParametrizedTypes)
        ]
    }

    def "should parse not specific parametrized types"(){
        when:
        def result = testObj.parse(NestedGenerics.getMethod("getGenerics")).get()

        then:
        result.fieldName == "generics"
        result.type == GET
        result.prefix == "get"
        result.declaredType == Map
        result.parsedTypes == [
                new GenericType(String.name, of(String), getChildrenParametrizedTypes),
                new GenericType('T', empty(), getChildrenParametrizedTypes)
        ]
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
        def result = testObj.parse(SimpleEntity).collect(toList())

        then:
        result.size() == 4
        result.get(0).mapFieldName == "field1"
        result.get(1).mapFieldName == "withGenerics"
        result.get(2).mapFieldName == "id"
        result.get(3).mapFieldName == "generics"
    }

    def "should parse all correctly" (){
        when:
        def result = testObj.parseAll(SimpleEntity)

        then:
        result.entrySet().size() == 6
        result.get(SimpleEntity.getMethod("getField1")).get() == new FieldAccessor(
                "getField1",
                "field1",
                "get",
                GET, returnType
                , annotations
        )
        result.get(SimpleEntity.getMethod("getWithGenerics")).get() == new FieldAccessor(
                "getWithGenerics",
                "withGenerics",
                "get",
                GET, returnType
                , annotations
        )
        result.get(SimpleEntity.getMethod("withGenerics", Map)).get() == new FieldAccessor(
                "withGenerics",
                "generics",
                "with",
                BUILDER, returnType
                , annotations
        )
        result.get(SimpleEntity.getMethod("getId")).get() == new FieldAccessor(
                "getId",
                "id",
                "get",
                GET, returnType
                , annotations
        )
        ! result.get(SimpleEntity.getMethod("asDbo")).isPresent()
        ! result.get(SimpleEntity.getMethod("dataEquals", Entity)).isPresent()
    }

    static interface SimpleEntity extends Entity{
        public String getField1();
        public Map<String, SimpleEntity> getWithGenerics()
        public SimpleEntity withGenerics (Map<String, SimpleEntity> generics)
    }

    static interface NestedGenerics<T>{
        public Map<String, T> getGenerics ()
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
