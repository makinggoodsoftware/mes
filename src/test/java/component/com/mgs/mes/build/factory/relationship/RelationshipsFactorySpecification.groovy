package com.mgs.mes.build.factory.relationship
import com.mgs.config.ReflectionConfig
import com.mgs.config.mes.build.BuildConfig
import com.mgs.mes.context.MongoContext
import com.mgs.mes.context.MongoContextReference
import com.mgs.mes.db.EntityRetriever
import com.mgs.mes.entityA.EntityA
import com.mgs.mes.entityA.EntityARelationships
import com.mgs.mes.entityC.EntityC
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityC
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityCBuilder
import org.bson.types.ObjectId
import spock.lang.Specification

import static java.util.Optional.of

class RelationshipsFactorySpecification extends Specification {
    RelationshipsFactory<EntityA, EntityARelationships> testObj

    EntityA a = Mock(EntityA)
    EntityC c = Mock(EntityC)
    EntityRetriever<EntityA> retrieverA = Mock (EntityRetriever)
    EntityRetriever<EntityC> retrieverC = Mock (EntityRetriever)
    ObjectId aIdMock = Mock (ObjectId)
    ObjectId cIdMock = Mock (ObjectId)
    MongoContextReference contextReferenceMock = Mock(MongoContextReference)
    MongoContext contextMock = Mock(MongoContext)

    BuildConfig buildConfig = new BuildConfig(new ReflectionConfig())

    def setup () {
        testObj = buildConfig.factories().relationships(EntityA, EntityARelationships, contextReferenceMock)

        a.id >> of(aIdMock)
        c.id >> of(cIdMock)

        contextReferenceMock.get() >> contextMock
        contextMock.getRelationshipBuilderFactory(EntityA_EntityCBuilder) >> buildConfig.factories().relationshipBuilder(EntityA_EntityC, EntityA_EntityCBuilder)
        contextMock.getRetriever(EntityA) >> retrieverA
        contextMock.getRetriever(EntityC) >> retrieverC
    }

    def "should create relationship" (){
        when:
        def result = testObj.from(a).hasEntityC(c).create()

        then:
        result.left.refId == aIdMock
        result.left.refName == "EntityA"
        result.right.refId == cIdMock
        result.right.refName == "EntityC"
    }
}