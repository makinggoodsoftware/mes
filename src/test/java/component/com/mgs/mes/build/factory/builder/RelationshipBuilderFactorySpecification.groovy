package com.mgs.mes.build.factory.builder

import com.mgs.config.mes.build.BuildConfig
import com.mgs.config.reflection.ReflectionConfig
import com.mgs.mes.db.EntityRetriever
import com.mgs.mes.entityA.EntityA
import com.mgs.mes.entityC.EntityC
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityC
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityCBuilder
import org.bson.types.ObjectId
import spock.lang.Specification

class RelationshipBuilderFactorySpecification extends Specification {
    RelationshipBuilderFactory<EntityA, EntityC, EntityA_EntityC, EntityA_EntityCBuilder> testObj
    BuildConfig buildConfig = new BuildConfig(new ReflectionConfig())

    EntityA entityA = Mock (EntityA)
    EntityRetriever<EntityA> retrieverA = Mock (EntityRetriever)
    EntityC entityC = Mock (EntityC)
    EntityRetriever<EntityC> retrieverC = Mock (EntityRetriever)

    ObjectId idAMock = Mock (ObjectId)
    ObjectId idCMock = Mock (ObjectId)

    def "setup" (){
        testObj = buildConfig.factories().relationshipBuilder(EntityA_EntityC, EntityA_EntityCBuilder)
    }

    def "should create a relationship builder" (){
        given:
        entityA.id >> Optional.of(idAMock)
        entityC.id >> Optional.of(idCMock)

        when:
        EntityA_EntityC result = testObj.newRelationshipBuilder(entityA, retrieverA, entityC, retrieverC).create()

        then:
        result.left.refId == idAMock
        result.left.refName == "EntityA"
        result.right.refId == idCMock
        result.right.refName == "EntityC"
    }
}
