package com.mgs.mes.db

import com.mgs.mes.entityB.EntityB
import com.mgs.mes.entityB.EntityBBuilder
import com.mgs.mes.entityB.EntityBRelationships
import com.mgs.mes.meta.init.*
import spock.lang.Specification

class DBSpecification extends Specification {
    MongoManager<EntityB, EntityBBuilder, EntityBRelationships> Bs

    def "setup" (){
        EntityDescriptorFactory descriptorFactory = new EntityDescriptorFactory ();
        MongoContext context = new MongoOrchestrator(new MongoDaoFactory(), new MongoInitializerFactory()).
                createContext(
                        "localhost", 27017, "testDb",
                        [descriptorFactory.create(EntityB, EntityBBuilder, EntityBRelationships)]
                )
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
