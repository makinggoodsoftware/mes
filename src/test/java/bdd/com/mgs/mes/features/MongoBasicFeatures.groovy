package com.mgs.mes.features
import com.mgs.mes.*
import com.mgs.mes.db.MongoPersister
import com.mgs.mes.db.MongoRetriever
import com.mgs.mes.model.builder.ModelBuilderFactory
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
        persister = factory.persister(EntityA, EntityABuilder)
        retriever = factory.retriever (EntityA)
        entityABuilder = factory.builder (EntityA, EntityABuilder)
        entityBBuilder = factory.builder (EntityB, EntityBBuilder)
        randomValue = UUID.randomUUID().toString()
    }

    def "shouldPerformCRUDInSimpleEntity" () {
        when:
        EntityA original = entityABuilder.createNew().
                            withEntityAfield1("value1").
                            withEntityAfield2( "value2").
                            withEmbedded(entityBBuilder.createNew().
                                    withEntityBfield1("entityAfield1").
                                    withEntityBfield2("entityAfield2").
                                    create()
                            ).
                            create()

        then:
        ! original.id.present

        //Test create
        when:
        def afterPersist = persister.create(original)

        then:
        !original.id.present
        afterPersist.id.present

        when:
        this.fromDb = retriever.byId (afterPersist.id.get()).get()

        then:
        this.fromDb != original
        this.fromDb == afterPersist

        //Test update
        when:
        EntityA updated = entityABuilder.update(afterPersist as EntityA).
                            withEntityAfield2("entityAfield2 new values").
                            withEmbedded(entityBBuilder.update(original.getEmbedded()).
                                    withEntityBfield1("new Field1").
                                    create()).
                            create()

        then:
        updated.id.get() == afterPersist.id.get()

        when:
        persister.update (updated)
        this.fromDb = retriever.byId (updated.id.get()).get()

        then:
        this.fromDb != original
        this.fromDb == updated
    }
}
