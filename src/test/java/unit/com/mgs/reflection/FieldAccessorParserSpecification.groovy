package com.mgs.reflection

import com.mgs.mes.EntityA
import com.mgs.mes.EntityABuilder
import spock.lang.Specification

import static FieldAccessorType.BUILDER
import static FieldAccessorType.GET


class FieldAccessorParserSpecification extends Specification {
    FieldAccessorParser testObj

    def "setup" (){
        testObj = new FieldAccessorParser(new BeanNamingExpert())
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
