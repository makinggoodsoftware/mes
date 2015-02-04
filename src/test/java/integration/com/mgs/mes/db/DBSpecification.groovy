package com.mgs.mes.db
import com.mgs.mes.EntityB
import com.mgs.mes.EntityBBuilder
import com.mgs.mes.EntityBRelationships
import com.mgs.mes.factory.MongoFactory
import com.mgs.mes.factory.MongoManager
import spock.lang.Specification

class DBSpecification extends Specification {
    MongoFactory mongoFactory = MongoFactory.from("localhost", "testDb", 27017)
    MongoManager<EntityB, EntityBBuilder, EntityBRelationships> Bs = mongoFactory.manager(EntityB, EntityBBuilder, EntityBRelationships)

    def "should save simple object into collection" (){
        given:
        EntityB b = Bs.builder.createNew().
                withEntityBfield1("value1").
                withEntityBfield2("value2").
                create()

        when:
        def savedObject = Bs.persister.create(b)

        then:
        savedObject != b


        when:
        def idFromDb = savedObject.getId().get()
        def fromDb = Bs.retriever.byId(idFromDb).get()

        then:
        fromDb == savedObject
        idFromDb == fromDb.getId().get()
    }
}
