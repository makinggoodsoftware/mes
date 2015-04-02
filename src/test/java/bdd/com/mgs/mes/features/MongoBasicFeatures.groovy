package com.mgs.mes.features

import com.mgs.config.MesConfigFactory
import com.mgs.mes.context.EntityDescriptor
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
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityC
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityCBuilder
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityCRelationships
import spock.lang.Specification

class MongoBasicFeatures extends Specification{
    EntityA fromDb
    MongoManager<EntityA, EntityABuilder, EntityARelationships> As;
    MongoManager<EntityB, EntityBBuilder, EntityBRelationships> Bs;
    MongoManager<EntityC, EntityCBuilder, EntityCRelationships> Cs;
    MongoManager<EntityA_EntityC, EntityA_EntityCBuilder, EntityA_EntityCRelationships> A_Cs;

    def "setup" () {
        MongoContext context =
                new MesConfigFactory().
                simple("localhost", 27017, "bddDb").
                mongoContext([
                    new EntityDescriptor<>(EntityA, EntityABuilder, EntityARelationships),
                    new EntityDescriptor<>(EntityB, EntityBBuilder, EntityBRelationships),
                    new EntityDescriptor<>(EntityC, EntityCBuilder, EntityCRelationships),
                    new EntityDescriptor<>(EntityA_EntityC, EntityA_EntityCBuilder, EntityA_EntityCRelationships),
                ]);
        Bs = context.manager(EntityB)
        As = context.manager(EntityA);
        Bs = context.manager(EntityB);
        Cs = context.manager(EntityC);
        A_Cs = context.manager(EntityA_EntityC);
    }

    def "should perform simple CRUD operations" () {
        when: "build new entity"
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

        when: "creating entity"
        def afterPersist = As.persister.touch(original)

        then:
        !original.id.present
        afterPersist.id.present

        when: "retrieving entity"
        this.fromDb = As.retriever.byId (afterPersist.id.get()).get()

        then:
        this.fromDb != original
        this.fromDb == afterPersist

        when: "updating entity"
        EntityA updated = As.builder.update(afterPersist as EntityA).
                            withEntityAfield2("entityAfield2 new values").
                            withEmbedded(Bs.builder.update(original.getEmbedded()).
                                    withEntityBfield1("new Field1").
                                    create()).
                            create()

        then:
        updated.id.get() == afterPersist.id.get()

        when: "updating database"
        As.persister.touch (updated)
        this.fromDb = As.retriever.byId (updated.id.get()).get()

        then:
        this.fromDb != original
        this.fromDb == updated
    }

    def "should save simple relationships" () {
        given:
        EntityA entityA = As.persister.touch(As.builder.newEntityBuilder().
                withEntityAfield1("value1").
                withEntityAfield2( "value2").
                withEmbedded(Bs.builder.newEntityBuilder().
                        withEntityBfield1("entityAfield1").
                        withEntityBfield2("entityAfield2").
                        create()
                ).
                create()
        )

        EntityC entityC = Cs.persister.touch(Cs.builder.newEntityBuilder().
                withEntityCfield1("valueC1").
                withEntityCfield2("valueC2").
                create()
        )

        when:
        EntityA_EntityC a_c = A_Cs.persister.touch(As.
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
