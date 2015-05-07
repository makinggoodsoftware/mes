package com.mgs.mes.v2.polymorphism

import com.mgs.mes.model.Entity
import com.mgs.reflection.*
import spock.lang.Specification

import static java.util.Optional.of

class PolymorphismManagerSpecification extends Specification {
    PolymorphismManager testObj

    FieldAccessorParser fieldAccessorParserMock = Mock (FieldAccessorParser)
    BeanNamingExpert beanNamingExpertMock = Mock (BeanNamingExpert)
    Reflections reflectionsMock = Mock (Reflections)

    def "setup" (){
        testObj = new PolymorphismManager(fieldAccessorParserMock, beanNamingExpertMock, reflectionsMock)
    }

    def "should analyse simple property" () {
        given:
        FieldAccessor fieldAccessorMock = Mock (FieldAccessor)
        beanNamingExpertMock.getGetterName("string") >> "getString"
        fieldAccessorParserMock.parse(SampleEntity, "getString") >> of(fieldAccessorMock)
        fieldAccessorMock.declaredType >> String

        when:
        PolymorphismDescriptor result = testObj.analise(SampleEntity, 'string')

        then:
        result.polymorphicTypes == [String]
        result.type == String
    }

    def "should analyse collection property" () {
        given:
        FieldAccessor fieldAccessorMock = Mock (FieldAccessor)
        beanNamingExpertMock.getGetterName("strings") >> "getStrings"
        fieldAccessorParserMock.parse(SampleEntity, "getStrings") >> of(fieldAccessorMock)
        fieldAccessorMock.declaredType >> List
        fieldAccessorMock.parametrizedTypes >> [new ParametrizedType("String", of(String), getChildrenParametrizedTypes)]
        reflectionsMock.isAssignableTo(List, Collection) >> true

        when:
        PolymorphismDescriptor result = testObj.analise(SampleEntity, 'strings')

        then:
        result.polymorphicTypes == [String]
        result.type == String
    }

    private static interface SampleEntity extends Entity {
        String getString ();
        List<String> getStrings ();
    }
}
