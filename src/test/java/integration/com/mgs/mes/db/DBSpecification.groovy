package com.mgs.mes.db
import com.mgs.mes.entityB.EntityB
import com.mgs.mes.entityB.EntityBBuilder
import com.mgs.mes.entityB.EntityBRelationships
import com.mgs.mes.factory.MongoContext
import com.mgs.mes.factory.MongoContextFactory
import com.mgs.mes.factory.MongoManager
import spock.lang.Specification

class DBSpecification extends Specification {
    MongoManager<EntityB, EntityBBuilder, EntityBRelationships> Bs

    def "setup" (){
        MongoContextFactory mongoContextFactory = MongoContextFactory.from("localhost", "testDb", 27017)
        mongoContextFactory.register(EntityB, EntityBBuilder, EntityBRelationships)
        MongoContext context = mongoContextFactory.create()
        Bs = context.manager(EntityB)
    }

    def "should save simple object into collection" (){
        given:
        EntityB b = Bs.builder.newEntityBuilder().
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
