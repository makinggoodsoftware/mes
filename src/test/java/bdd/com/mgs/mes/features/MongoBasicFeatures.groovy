package com.mgs.mes.features
import com.mgs.mes.EntityA
import com.mgs.mes.EntityABuilder
import com.mgs.mes.EntityB
import com.mgs.mes.EntityBBuilder
import com.mgs.mes.db.MongoPersister
import com.mgs.mes.factory.MongoFactory
import com.mgs.mes.factory.MongoManager
import spock.lang.Specification

import static com.mgs.mes.factory.MongoFactory.from

class MongoBasicFeatures extends Specification{
    MongoFactory factory;
    MongoPersister persister;
    String randomValue
    EntityA fromDb
    MongoManager<EntityA, EntityABuilder> As;
    MongoManager<EntityB, EntityBBuilder> Bs;

    def "setup" () {
        factory = from("localhost", "bddDb", 27017)
        As = factory.manager(EntityA, EntityABuilder)
        Bs = factory.manager(EntityB, EntityBBuilder)
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
}
