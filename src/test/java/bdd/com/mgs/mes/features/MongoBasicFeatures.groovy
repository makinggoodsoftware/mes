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
import com.mgs.mes.simpleModel.entityD.EntityD
import com.mgs.mes.simpleModel.entityD.EntityDBuilder
import com.mgs.mes.simpleModel.entityE.EntityE
import com.mgs.mes.simpleModel.entityE.EntityEBuilder
import com.mgs.mes.simpleModel.entityF.EntityF
import com.mgs.mes.simpleModel.entityF.EntityFBuilder
import spock.lang.Specification

class MongoBasicFeatures extends Specification{
    EntityA fromDb
    MongoManager<EntityA, EntityABuilder> As;
    MongoManager<EntityB, EntityBBuilder> Bs;
    MongoManager<EntityC, EntityCBuilder> Cs;
    MongoManager<EntityD, EntityDBuilder> Ds;
    MongoManager<EntityE, EntityEBuilder> Es;
    MongoManager<EntityF, EntityFBuilder> Fs;

    def "setup" () {
        MongoContext context = new MesConfigFactory().
                simple("localhost", 27017, "bddDb").
                mongoContext([
                    new EntityDescriptor<>(EntityA, EntityABuilder),
                    new EntityDescriptor<>(EntityB, EntityBBuilder),
                    new EntityDescriptor<>(EntityC, EntityCBuilder),
                    new EntityDescriptor<>(EntityD, EntityDBuilder),
                    new EntityDescriptor<>(EntityE, EntityEBuilder),
                    new EntityDescriptor<>(EntityF, EntityFBuilder),
                ]);
        As = context.manager(EntityA);
        Bs = context.manager(EntityB)
        Cs = context.manager(EntityC);
        Ds = context.manager(EntityD);
        Es = context.manager(EntityE);
        Fs = context.manager(EntityF);
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

    def "should perform CRUD in an embedded simple list" (){
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

    def "should perform CRUD in an embedded complex list" (){
        given:
        EntityB b1 = Bs.builder.newEntity().withEntityBfield1("field1").withEntityBfield2("field2").create()
        EntityB b2 = Bs.builder.newEntity().withEntityBfield1("field1").withEntityBfield2("field2").create()

        Bs.persister.touch(b1)
        Bs.persister.touch(b2)

        when:
        EntityD d = Ds.builder.newEntity().
                    withComplexList([b1, b2]).
                    create()

        then:
        d.complexList == [b1, b2]

        when:
        EntityD fromDb = Ds.persister.touch(d)

        then:
        fromDb.dataEquals(d)
    }

    def "should perform CRUD with OneToOne relationships" (){
        given:
        EntityB b = Bs.builder.newEntity().withEntityBfield1("value1").withEntityBfield2("value2").create()
        EntityB bFromDb = Bs.persister.touch(b)

        when:
        EntityE e = Es.builder.newEntity().withOneToOne(bFromDb).create()

        then:
        e.oneToOne.refId == bFromDb.getId().get()
        e.oneToOne.refName == "EntityB"
        e.oneToOne.retrieve() == bFromDb

        when:
        EntityE eFromDb = Es.persister.touch(e)

        then:
        eFromDb != e
        eFromDb.dataEquals(e)

        when:
        EntityE eRetrieved = Es.retriever.byId(eFromDb.getId().get()).get()

        then:
        eRetrieved == eFromDb

        when:
        EntityB b2 = Bs.builder.newEntity().withEntityBfield1("value1.2").withEntityBfield2("value2.2").create()
        EntityB b2FromDb = Bs.persister.touch(b2)
        EntityE e2 = Es.builder.update(eRetrieved).withOneToOne(b2FromDb).create()
        Es.persister.touch(e2)

        then:
        e2.oneToOne.refId == b2FromDb.getId().get()
        e2.oneToOne.refName == "EntityB"
        e2.oneToOne.retrieve() == b2FromDb
    }

    def "should perform CRUD with OneToMany relationships" (){
        given:
        EntityB b1 = Bs.builder.newEntity().withEntityBfield1("value1.1").withEntityBfield2("value2.1").create()
        EntityB b1FromDb = Bs.persister.touch(b1)
        EntityB b2 = Bs.builder.newEntity().withEntityBfield1("value1.2").withEntityBfield2("value2.2").create()
        EntityB b2FromDb = Bs.persister.touch(b2)

        when:
        EntityF f = Fs.builder.newEntity().withOneToMany([b1FromDb, b2FromDb]).create()

        then:
        f.oneToMany.retrieveAll() == [b1FromDb, b2FromDb]

        when:
        EntityF fromDb = Fs.persister.touch(f)

        then:
        fromDb.dataEquals(f)

        when:
        EntityF retrieved = Fs.retriever.byId(fromDb.getId().get()).get()

        then:
        retrieved == fromDb
    }
}