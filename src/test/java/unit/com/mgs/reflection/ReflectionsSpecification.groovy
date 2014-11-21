package com.mgs.reflection

import com.mgs.mes.EntityA
import com.mgs.mes.model.MongoEntity
import spock.lang.Specification
import spock.lang.Unroll


class ReflectionsSpecification extends Specification {
    Reflections testObj

    def "setup" (){
        testObj = new Reflections()
    }

    @Unroll("When type=[#type]|assignableTo=[#assignableTo] -> [#expectedResult]")
    def "should return if type is simple or assignable" (){
        when:
        def result = testObj.isSimpleOrAssignableTo(type, assignableTo)

        then:
        result == expectedResult

        where:
        type        | assignableTo  | expectedResult
        int         | Void          | true
        float       | Void          | true
        double      | Void          | true
        Integer     | Void          | true
        Float       | Void          | true
        Double      | Void          | true
        String      | Void          | true
        File        | Void          | false
        ArrayList   | List          | true
        HashMap     | List          | false
        EntityA     | MongoEntity   | true
    }
}
