package com.mgs.mes.context
import com.mgs.mes.context.unlinkedContext.UnlinkedMongoContext
import com.mgs.mes.db.MongoDao
import com.mgs.mes.services.core.EntityPersister
import com.mgs.mes.services.core.EntityRetriever
import com.mgs.mes.services.core.builder.EntityBuilderProvider
import com.mgs.mes.services.core.reference.EntityReferenceProvider
import com.mgs.mes.services.factory.EntityBuilderProviderFactory
import com.mgs.mes.services.factory.EntityReferenceProviderFactory
import com.mgs.mes.services.factory.MongoPersisterFactory
import com.mgs.mes.simpleModel.entityA.EntityA
import com.mgs.mes.simpleModel.entityA.EntityABuilder
import com.mgs.mes.simpleModel.entityB.EntityB
import com.mgs.mes.simpleModel.entityB.EntityBBuilder
import spock.lang.Specification

class MongoContextFactorySpecification extends Specification {
    MongoContextFactory testObj
    MongoPersisterFactory mongoPersisterFactoryMock = Mock (MongoPersisterFactory)
    EntityReferenceProviderFactory entityReferenceProviderFactoryMock = Mock (EntityReferenceProviderFactory)
    EntityBuilderProviderFactory entityBuilderProviderFactoryMock = Mock (EntityBuilderProviderFactory)
    MongoDao mongoDaoMock = Mock (MongoDao)
    EntityReferenceProvider entityReferenceProviderMock = Mock (EntityReferenceProvider)

    EntityDescriptor<EntityA, EntityABuilder> entityADescriptor = new EntityDescriptor<>(EntityA, EntityABuilder)
    EntityDescriptor<EntityB, EntityBBuilder> entityBDescriptor = new EntityDescriptor<>(EntityB, EntityBBuilder)

    EntityRetriever<EntityA> retrieverA = Mock (EntityRetriever)
    EntityRetriever<EntityB> retrieverB = Mock (EntityRetriever)

    Map<Class, EntityRetriever> retrieverMap = [
            (EntityA): retrieverA,
            (EntityB): retrieverB,
    ]

    EntityPersister<EntityA, EntityABuilder> persisterA = Mock (EntityPersister)
    EntityPersister<EntityB, EntityBBuilder> persisterB = Mock (EntityPersister)
    EntityBuilderProvider<EntityA, EntityABuilder> builderA = Mock (EntityBuilderProvider)
    EntityBuilderProvider<EntityB, EntityBBuilder> builderB = Mock (EntityBuilderProvider)

    def "setup"  () {
        testObj = new MongoContextFactory(
                mongoPersisterFactoryMock,
                entityReferenceProviderFactoryMock,
                entityBuilderProviderFactoryMock
        )

        entityReferenceProviderFactoryMock.create(retrieverMap) >> entityReferenceProviderMock

        mongoPersisterFactoryMock.create(mongoDaoMock, entityReferenceProviderMock, EntityA, EntityABuilder) >> persisterA
        mongoPersisterFactoryMock.create(mongoDaoMock, entityReferenceProviderMock, EntityB, EntityBBuilder) >> persisterB

        entityBuilderProviderFactoryMock.builder(entityReferenceProviderMock, EntityA, EntityABuilder) >> builderA
        entityBuilderProviderFactoryMock.builder(entityReferenceProviderMock, EntityB, EntityBBuilder) >> builderB
    }

    def "should create a mongo context" (){
        when:
        MongoContext context = testObj.create(new UnlinkedMongoContext(
                mongoDaoMock,
                [
                    entityADescriptor,
                    entityBDescriptor,
                ] as Set,
               retrieverMap
        ))

        then:
        context.manager(EntityA).retriever == retrieverA
        context.manager(EntityA).persister == persisterA
        context.manager(EntityA).builder == builderA

        context.manager(EntityB).retriever == retrieverB
        context.manager(EntityB).persister == persisterB
        context.manager(EntityB).builder == builderB
    }
}
