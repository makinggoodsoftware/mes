package com.mgs.mes.utils

import com.mgs.mes.meta.Entities
import spock.lang.Specification

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

import static java.lang.reflect.Proxy.newProxyInstance

class MongoEntitiesSpecification extends Specification {
    Entities testObj = new Entities()

    def "should get collection name for simple interface" (){
        when:
        def result = testObj.collectionName(Entity)

        then:
        result == "Entity"
    }

    def "should work with proxy classes" (){
        given:
        def proxied = newProxyInstance(
                MongoEntitiesSpecification.getClassLoader(),
                [Entity].toArray() as Class<?>[],
                new InvocationHandler() {
                    @Override
                    Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return null
                    }
                }
        );

        when:
        def result = testObj.collectionName((Class<? extends Entity>) proxied.getClass())

        then:
        result == "Entity"
    }

    public static interface Entity extends com.mgs.mes.model.Entity {}
}
