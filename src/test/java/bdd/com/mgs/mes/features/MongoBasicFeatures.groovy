package com.mgs.mes.features
import com.mgs.config.MesConfigFactory
import com.mgs.mes.context.EntityDescriptor
import com.mgs.mes.context.MongoContext
import com.mgs.mes.context.MongoManager
import com.mgs.mes.simpleModel.entityA.EntityA
import com.mgs.mes.simpleModel.entityA.EntityABuilder
import com.mgs.mes.simpleModel.entityB.EntityB
import com.mgs.mes.simpleModel.entityB.EntityBBuilder
import com.mgs.mes.simpleModel.entityC.EntityC
import com.mgs.mes.simpleModel.entityC.EntityCBuilder
import spock.lang.Specification

class MongoBasicFeatures extends Specification{
    EntityA fromDb
    MongoManager<EntityA, EntityABuilder> As;
    MongoManager<EntityB, EntityBBuilder> Bs;
    MongoManager<EntityC, EntityCBuilder> Cs;

    def "setup" () {
        MongoContext context = new MesConfigFactory().
                simple("localhost", 27017, "bddDb").
                mongoContext([
                    new EntityDescriptor<>(EntityA, EntityABuilder),
                    new EntityDescriptor<>(EntityB, EntityBBuilder),
                    new EntityDescriptor<>(EntityC, EntityCBuilder),
                ]);
        As = context.manager(EntityA);
        Bs = context.manager(EntityB)
        Cs = context.manager(EntityC);
    }

    def "should perform simple CRUD operations" () {
        when: "build new entity"
        EntityA original = As.builder.newEntity().
                            withEntityAfield1("value1").
                            withEntityAfield2( "value2").
                            withEmbedded(Bs.builder.newEntity().
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

    def "should perform CRUD in an embedded list" (){
        when:
        EntityC c = Cs.builder.newEntity().
                withList(['item1', 'item2']).
                withString('string').
                create()

        then:
        c.list == ['item1', 'item2']
        c.string == 'string'

        when:
        EntityC afterSaving = Cs.persister.touch(c)

        then:
        afterSaving.dataEquals (c)

        when:
        EntityC afterRetrieving = Cs.retriever.byId(afterSaving.getId().get()).get()

        then:
        afterRetrieving.dataEquals (afterSaving)

        when:
        EntityC updated = Cs.builder.update(afterSaving).
            withList(['item3']).
            create()

        then:
        !updated.dataEquals(c)
        updated.list == ['item3']

        when:
        EntityC afterSaving2 = Cs.persister.touch(updated)

        then:
        afterSaving2.dataEquals (updated)

        when:
        EntityC afterRetrieving2 = Cs.retriever.byId(afterSaving2.getId().get()).get()

        then:
        afterRetrieving2.dataEquals (afterSaving2)
    }
}