package com.mgs.mes.db
import com.mgs.config.MesConfigFactory
import com.mgs.mes.context.EntityDescriptor
import com.mgs.mes.context.MongoContext
import com.mgs.mes.context.MongoManager
import com.mgs.mes.simpleModel.entityB.EntityB
import com.mgs.mes.simpleModel.entityB.EntityBBuilder
import com.mgs.mes.simpleModel.entityB.EntityBRelationships
import spock.lang.Specification

class DBSpecification extends Specification {
    MongoManager<EntityB, EntityBBuilder, EntityBRelationships> Bs

    def "setup" (){
        MongoContext context =
                new MesConfigFactory().
                        simple("localhost", 27017, "testDb").
                        mongoContext([
                                new EntityDescriptor<>(EntityB, EntityBBuilder, EntityBRelationships),
                        ]);
        Bs = context.manager(EntityB)
    }

    def "should save simple object into collection" (){
        given:
        EntityB b = Bs.builder.newEntityBuilder().
                withEntityBfield1("value1").
                withEntityBfield2("value2").
                create()

        when:
        def savedObject = Bs.persister.touch(b)

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
