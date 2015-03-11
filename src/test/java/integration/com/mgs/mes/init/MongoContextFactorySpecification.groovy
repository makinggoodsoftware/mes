package com.mgs.mes.init
import com.mgs.mes.entityA.EntityA
import com.mgs.mes.entityA.EntityABuilder
import com.mgs.mes.entityA.EntityARelationships
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityC
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityCBuilder
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityCRelationships
import spock.lang.Specification

class MongoContextFactorySpecification extends Specification {
    UnlinkedMongoContextRegistrer mongoContextFactory

    def "setup" (){
        mongoContextFactory = UnlinkedMongoContextRegistrer.from("localhost", "testDb", 27017)
    }

    def "should create context" (){
        given:
        mongoContextFactory.register(EntityA, EntityABuilder, EntityARelationships)
        mongoContextFactory.register(EntityA_EntityC, EntityA_EntityCBuilder, EntityA_EntityCRelationships)

        when:
        MongoContext context = mongoContextFactory.create()

        then:
        context.manager(EntityA) != null
    }
}
