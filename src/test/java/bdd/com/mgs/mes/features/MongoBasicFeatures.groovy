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
    ModelBuilderFactory<EntityA, EntityABuilder> entityABuilderFactory
    ModelBuilderFactory<EntityB, EntityBBuilder> entityBBuilderFactory

    def "setup" () {
        factory = from("", "", -1)
        persister = factory.getPersister()
        retriever = factory.retriever (EntityA)
        entityABuilderFactory = factory.builderFactory (EntityA, EntityABuilder)
        entityBBuilderFactory = factory.builderFactory (EntityB, EntityBBuilder)
        randomValue = UUID.randomUUID().toString()
    }

    def "shouldPerformCRUDInSimpleEntity" () {
        given:
        EntityA model = entityABuilderFactory.newEntityBuilder().
                            withEntityAfield1("value1").
                            withEntityAfield2( "value2").
                            withEmbedded(entityBBuilderFactory.newEntityBuilder().
                                    withEntityBfield1("entityAfield1").
                                    withEntityBfield2("entityAfield2").
                                    create()
                            ).
                            create()

        //Test create
        when:
        String id = persister.create(model)

        then:
        id != null

        when:
        fromDb = retriever.byId (id)

        then:
        fromDb == model

        //Test update
        when:
        EntityA updated = entityABuilderFactory.newEntityBuilderFrom(model).
                            withEntityAfield2("entityAfield2 new values").
                            withEmbedded(entityBBuilderFactory.newEntityBuilderFrom(model.getEmbedded()).
                                    withEntityBfield1("new Field1").
                                    create()).
                            create()

        persister.update (updated)
        fromDb = retriever.byId
        (id)

        then:
        fromDb != model
        fromDb == updated
    }
}
