package com.mgs.mes.features

import com.mgs.mes.context.EntityDescriptorFactory
import com.mgs.mes.context.MongoContext
import com.mgs.mes.context.MongoManager
import com.mgs.mes.entityA.EntityA
import com.mgs.mes.entityA.EntityABuilder
import com.mgs.mes.entityA.EntityARelationships
import com.mgs.mes.entityB.EntityB
import com.mgs.mes.entityB.EntityBBuilder
import com.mgs.mes.entityB.EntityBRelationships
import com.mgs.mes.entityC.EntityC
import com.mgs.mes.entityC.EntityCBuilder
import com.mgs.mes.entityC.EntityCRelationships
import com.mgs.mes.meta.init.MongoDaoFactory
import com.mgs.mes.meta.init.MongoInitializerFactory
import com.mgs.mes.meta.init.MongoOrchestrator
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityC
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityCBuilder
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityCRelationships
import spock.lang.Specification

class MongoBasicFeatures extends Specification{
    String randomValue
    EntityA fromDb
    MongoManager<EntityA, EntityABuilder, EntityARelationships> As;
    MongoManager<EntityB, EntityBBuilder, EntityBRelationships> Bs;
    MongoManager<EntityC, EntityCBuilder, EntityCRelationships> Cs;
    MongoManager<EntityA_EntityC, EntityA_EntityCBuilder, EntityA_EntityCRelationships> A_Cs;

    def "setup" () {
        EntityDescriptorFactory descriptorFactory = new EntityDescriptorFactory ();
        MongoContext context = new MongoOrchestrator(new MongoDaoFactory(), new MongoInitializerFactory()).
                createContext(
                        "localhost", 27017, "bddDb",
                        [
                                descriptorFactory.create(EntityA, EntityABuilder, EntityARelationships),
                                descriptorFactory.create(EntityB, EntityBBuilder, EntityBRelationships),
                                descriptorFactory.create(EntityC, EntityCBuilder, EntityCRelationships),
                                descriptorFactory.create(EntityA_EntityC, EntityA_EntityCBuilder, EntityA_EntityCRelationships),
                        ]
                )
        Bs = context.manager(EntityB)
        As = context.manager(EntityA);
        Bs = context.manager(EntityB);
        Cs = context.manager(EntityC);
        A_Cs = context.manager(EntityA_EntityC);
        randomValue = UUID.randomUUID().toString()
    }

    def "shouldPerformCRUDInSimpleEntity" () {
        when:
        EntityA original = As.builder.newEntityBuilder().
                            withEntityAfield1("value1").
                            withEntityAfield2( "value2").
                            withEmbedded(Bs.builder.newEntityBuilder().
                                    withEntityBfield1("entityAfield1").
                                    withEntityBfield2("entityAfield2").
                                    create()
                            ).
                            create()

        then:
        !original.id.present

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
        EntityA entityA = As.persister.create(As.builder.newEntityBuilder().
                withEntityAfield1("value1").
                withEntityAfield2( "value2").
                withEmbedded(Bs.builder.newEntityBuilder().
                        withEntityBfield1("entityAfield1").
                        withEntityBfield2("entityAfield2").
                        create()
                ).
                create()
        )

        EntityC entityC = Cs.persister.create(Cs.builder.newEntityBuilder().
                withEntityCfield1("valueC1").
                withEntityCfield2("valueC2").
                create()
        )

        when:
        EntityA_EntityC a_c = A_Cs.persister.create(As.
                                relationshipFrom(entityA).
                                hasEntityC(entityC).
                                create()
        )

        then:
        a_c.id.isPresent()
        a_c.left.retrieve() == entityA
        a_c.right.retrieve() == entityC
    }
}
