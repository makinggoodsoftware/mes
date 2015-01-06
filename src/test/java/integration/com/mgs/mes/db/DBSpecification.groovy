package com.mgs.mes.db

import com.mgs.mes.EntityB
import com.mgs.mes.EntityBBuilder
import com.mgs.mes.MongoFactory
import com.mgs.mes.orm.modelBuilder.ModelBuilderFactory
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
        def objectID = mongoFactory.getPersister().create(b)
        def fromDb = mongoFactory.retriever(EntityB).byId(objectID).get()

        then:
        fromDb == b
        objectID == fromDb.getId().get()
    }
}
