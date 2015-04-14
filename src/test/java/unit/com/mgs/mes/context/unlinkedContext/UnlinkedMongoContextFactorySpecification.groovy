package com.mgs.mes.context.unlinkedContext
import com.mgs.mes.build.factory.core.EntityRetrieverFactory
import com.mgs.mes.context.EntityDescriptor
import com.mgs.mes.db.EntityRetriever
import com.mgs.mes.db.MongoDao
import com.mgs.mes.meta.utils.Validator
import com.mgs.mes.simpleModel.entityA.EntityA
import com.mgs.mes.simpleModel.entityA.EntityABuilder
import com.mgs.mes.simpleModel.entityB.EntityB
import com.mgs.mes.simpleModel.entityB.EntityBBuilder
import spock.lang.Specification

class UnlinkedMongoContextFactorySpecification extends Specification {
    UnlinkedMongoContextFactory testObj
    MongoDao mongoDaoMock = Mock(MongoDao)
    Validator validatorMock = Mock (Validator)
    EntityRetrieverFactory entityRetrieverFactoryMock = Mock (EntityRetrieverFactory)

    EntityDescriptor<EntityA, EntityABuilder> entityADescriptor = new EntityDescriptor<>(EntityA, EntityABuilder)
    EntityRetriever<EntityA> entityARetrieverMock = Mock (EntityRetriever)

    EntityDescriptor<EntityB, EntityBBuilder> entityBDescriptor = new EntityDescriptor<>(EntityB, EntityBBuilder)
    EntityRetriever<EntityB> entityBRetrieverMock = Mock (EntityRetriever)

    def "setup" (){
        testObj = new UnlinkedMongoContextFactory(
                mongoDaoMock,
                validatorMock,
                entityRetrieverFactoryMock
        )
        entityRetrieverFactoryMock.createRetriever(mongoDaoMock, entityADescriptor) >> entityARetrieverMock
        entityRetrieverFactoryMock.createRetriever(mongoDaoMock, entityBDescriptor) >> entityBRetrieverMock
    }

    def "should create unlinked context" (){
        when:
        UnlinkedMongoContext unlinkedMongoContext = testObj.createUnlinkedContext([
                entityADescriptor,
                entityBDescriptor,
        ])

        then:
        unlinkedMongoContext.mongoDao.is (mongoDaoMock)
        unlinkedMongoContext.retrieverMap == [
                (EntityA): entityARetrieverMock,
                (EntityB): entityBRetrieverMock,
        ]
        unlinkedMongoContext.descriptors == [entityBDescriptor, entityADescriptor] as Set
    }
}
