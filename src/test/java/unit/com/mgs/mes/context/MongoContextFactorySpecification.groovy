package com.mgs.mes.context
import com.mgs.mes.build.factory.builder.EntityBuilderFactory
import com.mgs.mes.build.factory.builder.RelationshipBuilderFactory
import com.mgs.mes.db.EntityRetriever
import com.mgs.mes.db.MongoPersister
import com.mgs.mes.entityA.EntityA
import com.mgs.mes.entityA.EntityABuilder
import com.mgs.mes.entityA.EntityARelationships
import com.mgs.mes.entityC.EntityC
import com.mgs.mes.entityC.EntityCBuilder
import com.mgs.mes.entityC.EntityCRelationships
import com.mgs.mes.meta.Entities
import com.mgs.mes.meta.unlinkedContext.UnlinkedEntity
import com.mgs.mes.meta.unlinkedContext.UnlinkedMongoContext
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityC
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityCBuilder
import com.mgs.mes.relationships.entityA_EntityC.EntityA_EntityCRelationships
import spock.lang.Specification

class MongoContextFactorySpecification extends Specification {
    MongoContextFactory testObj
    Entities entitiesMock = Mock (Entities)
    UnlinkedMongoContext unlinkedMongoContextMock = Mock (UnlinkedMongoContext)

    EntityRetriever<EntityA> aRetrieverMock = Mock (EntityRetriever)
    EntityRetriever<EntityC> cRetrieverMock = Mock (EntityRetriever)
    EntityRetriever<EntityA_EntityC> a_cRetrieverMock = Mock (EntityRetriever)

    MongoPersister<EntityA, EntityABuilder> aPersisterMock = Mock(MongoPersister)
    MongoPersister<EntityC, EntityCBuilder> cPersisterMock = Mock(MongoPersister)
    MongoPersister<EntityA_EntityC, EntityA_EntityCBuilder> a_cPersisterMock = Mock(MongoPersister)

    EntityBuilderFactory<EntityA, EntityABuilder> aBuilderFactoryMock = Mock (EntityBuilderFactory)
    EntityBuilderFactory<EntityC, EntityCBuilder> cBuilderFactoryMock = Mock (EntityBuilderFactory)
    RelationshipBuilderFactory<EntityA, EntityC, EntityA_EntityC, EntityA_EntityCBuilder> a_cBuilderFactoryMock = Mock (RelationshipBuilderFactory)

    EntityA_EntityCBuilder a_cEntityCBuilder = Mock (EntityA_EntityCBuilder)
    EntityA entityAMock = Mock (EntityA)
    EntityC entityCMock = Mock (EntityC)
    EntityA_EntityC entityA_CMok = Mock (EntityA_EntityC)

    def "setup" (){
        testObj = new MongoContextFactory(entitiesMock)

        entitiesMock.findBaseMongoEntityType(entityAMock.class) >> EntityA
        entitiesMock.findBaseMongoEntityType(entityCMock.class) >> EntityC

        unlinkedMongoContextMock.relationshipBuilderFactories >> [(EntityA_EntityCBuilder) : a_cBuilderFactoryMock]


        EntityDescriptor<EntityA, EntityABuilder, EntityARelationships> aDescriptor = new EntityDescriptor<EntityA, EntityABuilder, EntityARelationships>(
                EntityA,
                EntityABuilder,
                EntityARelationships
        )
        EntityDescriptor<EntityC, EntityCBuilder, EntityCRelationships> cDescriptor = new EntityDescriptor<EntityC, EntityCBuilder, EntityCRelationships>(
                EntityC,
                EntityCBuilder,
                EntityCRelationships
        )
        EntityDescriptor<EntityA_EntityC, EntityA_EntityCBuilder, EntityA_EntityCRelationships> a_cDescriptor = new EntityDescriptor<EntityA_EntityC, EntityA_EntityCBuilder, EntityA_EntityCRelationships>(
                EntityA_EntityC,
                EntityA_EntityCBuilder,
                EntityA_EntityCRelationships
        )
        unlinkedMongoContextMock.unlinkedEntities >> [
            (aDescriptor) : new UnlinkedEntity<EntityA, EntityABuilder, EntityARelationships> (
                        aRetrieverMock,
                        aPersisterMock,
                        aBuilderFactoryMock,
                        aDescriptor
                    ),
            (cDescriptor) : new UnlinkedEntity<EntityC, EntityCBuilder, EntityCRelationships> (
                        cRetrieverMock,
                        cPersisterMock,
                        cBuilderFactoryMock,
                        cDescriptor
            ),
            (a_cDescriptor) : new UnlinkedEntity<EntityA_EntityC, EntityA_EntityCBuilder, EntityA_EntityCRelationships> (
                        a_cRetrieverMock,
                        a_cPersisterMock,
                        a_cBuilderFactoryMock,
                        a_cDescriptor
            )
        ]

        a_cBuilderFactoryMock.newRelationshipBuilder(entityAMock, aRetrieverMock, entityCMock, cRetrieverMock) >>  a_cEntityCBuilder

    }

    def "should create context" (){
        given:
        MongoContext context = testObj.create(unlinkedMongoContextMock).get()

        when:
        MongoManager<EntityA, EntityABuilder, EntityARelationships> aManager = context.manager(EntityA)

        then:
        aManager.retriever == aRetrieverMock
        aManager.persister == aPersisterMock
        aManager.builder == aBuilderFactoryMock
        aManager.relationshipFrom(entityAMock).hasEntityC(entityCMock) == a_cEntityCBuilder

        when:
        MongoManager<EntityC, EntityCBuilder, EntityCRelationships> cManager = context.manager(EntityC)

        then:
        cManager.retriever == cRetrieverMock
        cManager.persister == cPersisterMock
        cManager.builder == cBuilderFactoryMock
        cManager.relationshipFrom(entityCMock).class.interfaces == [EntityCRelationships]

        when:
        MongoManager<EntityA_EntityC, EntityA_EntityCBuilder, EntityA_EntityCRelationships> a_cManager = context.manager(EntityA_EntityC)

        then:
        a_cManager.retriever == a_cRetrieverMock
        a_cManager.persister == a_cPersisterMock
        a_cManager.builder == a_cBuilderFactoryMock
        a_cManager.relationshipFrom(entityA_CMok).class.interfaces == [EntityA_EntityCRelationships]
    }
}