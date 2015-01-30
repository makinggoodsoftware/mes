package com.mgs.mes.db

import com.mgs.mes.EntityB
import com.mgs.mes.EntityBBuilder
import com.mgs.mes.MongoFactory
import com.mgs.mes.model.builder.ModelBuilderFactory
import spock.lang.Specification

class DBSpecification extends Specification {
    MongoFactory mongoFactory = MongoFactory.from("localhost", "testDb", 27017)
    ModelBuilderFactory<EntityB, EntityBBuilder> modelBuilderFactoryB = mongoFactory.builder(EntityB, EntityBBuilder)

    def "should save simple object into collection" (){
        given:
        EntityB b = modelBuilderFactoryB.createNew().
                withEntityBfield1("value1").
                withEntityBfield2("value2").
                create()

        when:
        def savedObject = mongoFactory.persister(EntityB, EntityBBuilder).create(b)

        then:
        savedObject != b


        when:
        def idFromDb = savedObject.getId().get()
        def fromDb = mongoFactory.retriever(EntityB).byId(idFromDb).get()

        then:
        fromDb == savedObject
        idFromDb == fromDb.getId().get()
    }
}
