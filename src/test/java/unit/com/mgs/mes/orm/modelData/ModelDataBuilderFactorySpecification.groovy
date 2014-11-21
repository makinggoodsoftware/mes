package com.mgs.mes.orm.modelData

import com.mgs.mes.model.MongoEntity
import com.mgs.mes.orm.ModelData
import com.mgs.reflection.BeanNamingExpert
import com.mgs.reflection.FieldAccessor
import com.mgs.reflection.FieldAccessorParser
import spock.lang.Specification

import static com.mgs.reflection.FieldAccessorType.GET
import static java.util.Optional.of

class ModelDataBuilderFactorySpecification extends Specification {
    ModelDataBuilderFactory testObj
    FieldAccessorParser fieldAccessorParserMock = Mock (FieldAccessorParser)
    Entity entityMok = Mock(Entity)
    BeanNamingExpert beanNamingExpertMock = Mock (BeanNamingExpert)
    ModelDataFactory modelDataFactoryMock = Mock (ModelDataFactory)
    ModelData resultMock = Mock (ModelData)

    FieldAccessor field1Accessor
    FieldAccessor field2Accessor

    def "setup" (){
        field1Accessor = new FieldAccessor(String, "getField1", "field1", "get", GET)
        field2Accessor = new FieldAccessor(String, "getField2", "field2", "get", GET)

        fieldAccessorParserMock.parse(Entity) >> [field1Accessor, field2Accessor].stream()
        fieldAccessorParserMock.parse(Entity, "getField1") >> of(field1Accessor)
        fieldAccessorParserMock.parse(Entity, "getField2") >> of(field2Accessor)

        beanNamingExpertMock.getGetterName("field1") >> "getField1"
        beanNamingExpertMock.getGetterName("field2") >> "getField2"
        beanNamingExpertMock.getGetterName("child") >> "getChild"

        testObj = new ModelDataBuilderFactory(modelDataFactoryMock, beanNamingExpertMock, fieldAccessorParserMock)

        entityMok.field1 >> "baseValue1"
    }

    def "should create empty model data builder" (){
        given:
        Map<FieldAccessor, Object> expectedMap = [
                (field1Accessor) : null,
                (field2Accessor) : null
        ]

        when:
        def result = testObj.empty(Entity).build()

        then:
        1 * modelDataFactoryMock.fromFieldAccessorMap (Entity, expectedMap) >> resultMock
        result.is(resultMock)
    }

    def "should create data builder from model" (){
        given:
        Map<FieldAccessor, Object> expectedMap = [
                (field1Accessor) : "baseValue1",
                (field2Accessor) : null
        ]

        when:
        def result = testObj.from(Entity, entityMok).build()

        then:
        1 * modelDataFactoryMock.fromFieldAccessorMap (Entity, expectedMap) >> resultMock
        result.is(resultMock)
    }

    def "should be updatable" (){
        given:
        Map<FieldAccessor, Object> expectedMap = [
                (field1Accessor) : "baseValue1",
                (field2Accessor) : "value2"
        ]

        when:
        def result = testObj.from(Entity, entityMok).with("field2", "value2").build()

        then:
        1 * modelDataFactoryMock.fromFieldAccessorMap (Entity, expectedMap) >> resultMock
        result.is(resultMock)
    }

    public static interface Entity extends MongoEntity {
        public String getField1();
        public String getField2();
    }
}
