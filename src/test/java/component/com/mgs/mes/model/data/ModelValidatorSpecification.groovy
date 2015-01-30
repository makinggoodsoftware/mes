package com.mgs.mes.model.data

import com.mgs.mes.model.ModelBuilder
import com.mgs.mes.model.ModelValidator
import com.mgs.mes.model.MongoEntity
import com.mgs.reflection.BeanNamingExpert
import com.mgs.reflection.FieldAccessorParser
import com.mgs.reflection.Reflections
import spock.lang.Specification
import spock.lang.Unroll

class ModelValidatorSpecification extends Specification {
    ModelValidator testObj
    FieldAccessorParser fieldAccessorParser = new FieldAccessorParser(new BeanNamingExpert())
    Reflections reflections = new Reflections()

    def "setup" (){
        testObj = new ModelValidator(reflections, fieldAccessorParser)
    }

    def "should validate interfaces" (){
        when:
        testObj.validate(Getter1, Builder1)

        then:
        notThrown Exception
    }

    @Unroll
    def "should flag interfaces as not valid when the combination of methods is not correct" (){
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

    def "TBC should fail if model is not flagged with the correct interface" (){
        expect:
        false
    }

    def "TBC should fail if builder is not flagged with the correct interface" (){
        expect:
        false
    }


    public static interface InvalidGetter extends MongoEntity{}

    public static interface Getter1 extends MongoEntity{
        String getField1();
        Getter2 getChild();
    }

    public static interface Builder1 extends ModelBuilder{
        Builder1 withField1 (String field1);
        Builder1 withChild (Getter2 child);
    }

    public static interface Getter2 extends MongoEntity{}

    public static interface Builder1Wrong1 extends ModelBuilder{
        Builder1Wrong1 withField1 (int field1);
        Builder1Wrong1 withChild (Getter2 child);
    }

    public static interface Builder1Wrong2 extends ModelBuilder{
        Builder1Wrong1 withField1 (String field1);
        Builder1Wrong1 withField2 (String field2);
        Builder1Wrong1 withChild (Getter2 child);
    }

    public static interface Builder2 extends ModelBuilder{
        Builder2 withField1 (String field1);
        Builder2 withField2 (String field2);
    }
}
