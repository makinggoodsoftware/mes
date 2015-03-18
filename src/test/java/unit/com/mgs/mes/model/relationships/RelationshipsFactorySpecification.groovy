package com.mgs.mes.model.relationships
import com.mgs.mes.entityA.EntityA
import com.mgs.mes.entityA.EntityARelationships
import com.mgs.mes.entityC.EntityC
import com.mgs.mes.model.builder.RelationshipBuilderFactory
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityC
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityCBuilder
import spock.lang.Specification

class RelationshipsFactorySpecification extends Specification {
    RelationshipsFactory<EntityA, EntityARelationships> testObj
    EntityA a = Mock(EntityA)
    EntityC c = Mock(EntityC)
    EntityA_EntityCBuilder a_cBuilder = Mock(EntityA_EntityCBuilder)
    EntityA_EntityC a_c = Mock (EntityA_EntityC)

    def setup () {
        def a_cBuilderFactory = Mock(RelationshipBuilderFactory)
        def relationshipBuilderFactoriesByType = [
            (EntityA_EntityCBuilder): a_cBuilderFactory
        ]
        a_cBuilderFactory.newRelationshipBuilder(a, null, c, retrieverB) >> a_cBuilder
        a_cBuilder.create() >> a_c
        testObj = new RelationshipsFactory(EntityARelationships, context, entities)
    }

    def "should create relationships" (){
        when:
        def result = testObj.from(a).hasEntityC(c).create()

        then:
        result == a_c
    }
}