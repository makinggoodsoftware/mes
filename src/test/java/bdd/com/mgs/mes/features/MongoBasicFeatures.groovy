package com.mgs.mes.features
import com.mgs.mes.*
import com.mgs.mes.db.MongoPersister
import com.mgs.mes.db.MongoRetriever
import com.mgs.mes.orm.modelBuilder.ModelBuilderFactory
import spock.lang.Specification

import static com.mgs.mes.MongoFactory.from

class MongoBasicFeatures extends Specification{
    MongoFactory factory;
    MongoPersister persister;
    MongoRetriever<EntityA> retriever;
    String randomValue
    EntityA fromDb
    ModelBuilderFactory<EntityA, EntityABuilder> entityABuilder
    ModelBuilderFactory<EntityB, EntityBBuilder> entityBBuilder

    def "setup" () {
        factory = from("localhost", "bddDb", 27017)
        persister = factory.getPersister()
        retriever = factory.retriever (EntityA)
        entityABuilder = factory.builder (EntityA, EntityABuilder)
        entityBBuilder = factory.builder (EntityB, EntityBBuilder)
        randomValue = UUID.randomUUID().toString()
    }

    def "shouldPerformCRUDInSimpleEntity" () {
        given:
        EntityA original = entityABuilder.createNew().
                            withEntityAfield1("value1").
                            withEntityAfield2( "value2").
                            withEmbedded(entityBBuilder.createNew().
                                    withEntityBfield1("entityAfield1").
                                    withEntityBfield2("entityAfield2").
                                    create()
                            ).
                            create()

        //Test create
        when:
        def id = persister.create(original)

        then:
        id != null

        when:
        fromDb = retriever.byId (id).get()

        then:
        fromDb == original

        //Test update
        when:
        EntityA updated = entityABuilder.update(fromDb).
                            withEntityAfield2("entityAfield2 new values").
                            withEmbedded(entityBBuilder.update(original.getEmbedded()).
                                    withEntityBfield1("new Field1").
                                    create()).
                            create()

        then:
        updated.getId().isPresent()
        updated.getId().get() == id

        when:
        persister.update (updated)
        fromDb = retriever.byId (id).get()

        then:
        fromDb != original
        fromDb == updated
    }
}
