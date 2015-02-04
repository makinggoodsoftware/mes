package com.mgs.mes.features

import com.mgs.mes.*
import com.mgs.mes.factory.MongoFactory
import com.mgs.mes.factory.MongoManager
import spock.lang.Specification

import static com.mgs.mes.factory.MongoFactory.from

class MongoBasicFeatures extends Specification{
    MongoFactory factory;
    String randomValue
    EntityA fromDb
    MongoManager<EntityA, EntityABuilder, EntityARelationships> As;
    MongoManager<EntityB, EntityBBuilder, EntityBRelationships> Bs;
    MongoManager<EntityC, EntityCBuilder, EntityCRelationships> Cs;
    MongoManager<EntityA_EntityC, EntityA_EntityCBuilder, EntityA_EntityCRelationships> A_Cs;

    def "setup" () {
        factory = from("localhost", "bddDb", 27017)
        As = factory.manager(EntityA, EntityABuilder, EntityARelationships)
        Bs = factory.manager(EntityB, EntityBBuilder, EntityBRelationships)
        Cs = factory.manager(EntityC, EntityCBuilder, EntityCRelationships)
        A_Cs = factory.manager(EntityA_EntityC, EntityA_EntityCBuilder, EntityA_EntityCRelationships)
        randomValue = UUID.randomUUID().toString()
    }

    def "shouldPerformCRUDInSimpleEntity" () {
        when:
        EntityA original = As.builder.createNew().
                            withEntityAfield1("value1").
                            withEntityAfield2( "value2").
                            withEmbedded(Bs.builder.createNew().
                                    withEntityBfield1("entityAfield1").
                                    withEntityBfield2("entityAfield2").
                                    create()
                            ).
                            create()

        then:
        ! original.id.present

        //Test create
        when:
        def afterPersist = As.persister.create(original)

        then:
        !original.id.present
        afterPersist.id.present

        when:
        this.fromDb = As.retriever.byId (afterPersist.id.get()).get()

        then:
        this.fromDb != original
        this.fromDb == afterPersist

        //Test update
        when:
        EntityA updated = As.builder.update(afterPersist as EntityA).
                            withEntityAfield2("entityAfield2 new values").
                            withEmbedded(Bs.builder.update(original.getEmbedded()).
                                    withEntityBfield1("new Field1").
                                    create()).
                            create()

        then:
        updated.id.get() == afterPersist.id.get()

        when:
        As.persister.update (updated)
        this.fromDb = As.retriever.byId (updated.id.get()).get()

        then:
        this.fromDb != original
        this.fromDb == updated
    }

    def "should save relationships" () {
        given:
        EntityA entityA = As.builder.createNew().
                withEntityAfield1("value1").
                withEntityAfield2( "value2").
                withEmbedded(Bs.builder.createNew().
                        withEntityBfield1("entityAfield1").
                        withEntityBfield2("entityAfield2").
                        create()
                ).
                create()
        EntityC entityC = Cs.builder.createNew().
                withEntityCfield1("valueC1").
                withEntityCfield2("valueC2").
                create()

        when:
        EntityA_EntityC a_c = A_Cs.persister.create(As.
                                relationshipFrom(entityA).
                                hasEntityC(entityC).
                                create()
        )

        then:
        a_c.entityA == entityA
        a_c.entityC == entityC
    }
}
