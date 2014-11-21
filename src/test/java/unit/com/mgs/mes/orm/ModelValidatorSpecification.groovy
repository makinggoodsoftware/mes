package com.mgs.mes.orm

import com.mgs.mes.model.MongoEntity
import com.mgs.reflection.FieldAccessor
import com.mgs.reflection.FieldAccessorParser
import com.mgs.reflection.Reflections
import spock.lang.Specification

import static com.mgs.reflection.FieldAccessorType.BUILDER
import static com.mgs.reflection.FieldAccessorType.GET
import static java.util.Optional.empty
import static java.util.Optional.of

class ModelValidatorSpecification extends Specification {
    ModelValidator testObj
    FieldAccessorParser fieldAccessorParserMock = Mock (FieldAccessorParser)
    Reflections reflectionsMock = Mock (Reflections)

    def "setup" (){
        testObj = new ModelValidator(reflectionsMock, fieldAccessorParserMock)

        fieldAccessorParserMock.parseAll(Getter1) >> [
                of(new FieldAccessor(String, "getField1", "field1", "get", GET)),
                of(new FieldAccessor(Getter2, "getChild", "child", "get", GET)),
        ].stream()

        fieldAccessorParserMock.parseAll(Builder1) >> [
                of(new FieldAccessor(String, "withField1", "field1", "with", BUILDER)),
                of(new FieldAccessor(Getter2, "withChild", "child", "with", BUILDER))
        ].stream()

        fieldAccessorParserMock.parseAll(Builder2) >> [
                of(new FieldAccessor(String, "withField1", "field1", "with", BUILDER)),
                of(new FieldAccessor(String, "withField2", "field2", "with", BUILDER))
        ].stream()

        fieldAccessorParserMock.parseAll(Builder1Wrong1) >> [
                of(new FieldAccessor(int, "withField1", "field1", "with", BUILDER)),
                of(new FieldAccessor(Getter2, "withChild", "child", "with", BUILDER))
        ].stream()

        fieldAccessorParserMock.parseAll(Builder1Wrong2) >> [
                of(new FieldAccessor(String, "withField1", "field1", "with", BUILDER)),
                of(new FieldAccessor(String, "withField2", "field2", "with", BUILDER)),
                of(new FieldAccessor(Getter2, "withChild", "child", "with", BUILDER))
        ].stream()


        fieldAccessorParserMock.parseAll(InvalidGetter) >> [
                empty()
        ].stream()


        reflectionsMock.isSimpleOrAssignableTo(String, MongoEntity) >> true
        reflectionsMock.isSimpleOrAssignableTo(Getter2, MongoEntity) >> true
    }

    def "should validate interfaces" (){
        when:
        testObj.validate(Getter1, Builder1)

        then:
        notThrown Exception
    }

    def "should flag interfaces as not valid" (){
        when:
        testObj.validate(getter, setter)

        then:
        thrown IllegalArgumentException

        where:
        getter          | setter
        Getter1         | Builder2
        InvalidGetter   | Builder2
        Getter1         | Builder1Wrong1
        Getter1         | Builder1Wrong2
    }

    public static interface InvalidGetter {}

    public static interface Getter1 {}

    public static interface Builder1 {}

    public static interface Getter2 {}

    public static interface Builder1Wrong1 {}

    public static interface Builder1Wrong2 {}

    public static interface Builder2 {}
}
