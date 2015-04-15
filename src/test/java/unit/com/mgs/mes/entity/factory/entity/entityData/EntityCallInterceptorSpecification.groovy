package com.mgs.mes.entity.factory.entity.entityData

import com.mgs.mes.entity.data.EntityData
import com.mgs.mes.model.Entity
import com.mongodb.BasicDBObject
import spock.lang.Specification


class EntityCallInterceptorSpecification extends Specification {
    EntityCallInterceptor testObj
    EntityData thisEntityMock = Mock (EntityData)
    Entity otherEntityMock = Mock (Entity)

    def "setup" (){
        testObj = new EntityCallInterceptor(thisEntityMock)
    }

    def "should return true when comparing two equivalent entities" (){
        given:
        thisEntityMock.dbo >> new BasicDBObject().
                                append('_id', 'anId').
                                append('key1', 'value1').
                                append('key2', 'value2')

        otherEntityMock.asDbo() >> new BasicDBObject().
                                append('_id', 'differentId').
                                append('key1', 'value1').
                                append('key2', 'value2')

        expect:
        testObj.dataEquals(otherEntityMock)
    }

    def "should return false when comparing two entities that are equals but have diff amount of fields" (){
        given:
        thisEntityMock.dbo >> new BasicDBObject().
                append('_id', 'anId').
                append('key1', 'value1').
                append('key2', 'value2')

        otherEntityMock.asDbo() >> new BasicDBObject().
                append('_id', 'differentId').
                append('key1', 'value1').
                append('key2', 'value2').
                append('key3', 'value3')

        expect:
        ! testObj.dataEquals(otherEntityMock)
    }

    def "should return false when comparing two entities that are slightly different" (){
        given:
        thisEntityMock.dbo >> new BasicDBObject().
                append('_id', 'anId').
                append('key1', 'value1').
                append('key2', 'value2-a')

        otherEntityMock.asDbo() >> new BasicDBObject().
                append('_id', 'differentId').
                append('key1', 'value1').
                append('key2', 'value2')

        expect:
        ! testObj.dataEquals(otherEntityMock)
    }
}
