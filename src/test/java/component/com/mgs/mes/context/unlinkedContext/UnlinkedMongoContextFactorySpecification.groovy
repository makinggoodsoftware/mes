package com.mgs.mes.context.unlinkedContext
import com.mgs.config.ReflectionConfig
import com.mgs.config.mes.build.BuildConfig
import com.mgs.config.mes.context.ContextConfig
import com.mgs.config.mes.meta.MetaConfig
import com.mgs.mes.context.EntityDescriptor
import com.mgs.mes.db.MongoDao
import com.mgs.mes.entityA.EntityA
import com.mgs.mes.entityA.EntityABuilder
import com.mgs.mes.entityA.EntityARelationships
import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import org.bson.types.ObjectId
import spock.lang.Specification

import static java.util.Optional.of

class UnlinkedMongoContextFactorySpecification extends Specification {
    UnlinkedMongoContextFactory testObj
    ReflectionConfig reflectionConfig = new ReflectionConfig()
    MongoDao mongoDaoMock = Mock (MongoDao)
    ContextConfig contextConfig = new ContextConfig(
            new MetaConfig(reflectionConfig),
            new BuildConfig(reflectionConfig),
            reflectionConfig
    )
    EntityA entityAMock = Mock (EntityA)
    DBObject aDbObjectMock = Mock (DBObject)
    ObjectId aObjectIdMock = Mock (ObjectId)

    def "setup" (){
        testObj = contextConfig.unlinkedMongoContextFactory(mongoDaoMock)
        entityAMock.asDbo() >> aDbObjectMock
        entityAMock.id >> of(aObjectIdMock)
        aDbObjectMock.toMap() >> [:]
    }

    def "should create simple unlinked mongo context" () {
        given:
        EntityDescriptor<EntityA, EntityABuilder, EntityARelationships> aDescriptor = new EntityDescriptor<EntityA, EntityABuilder, EntityARelationships>(EntityA, EntityABuilder, EntityARelationships)

        when:
        UnlinkedMongoContext unlinkedContext = testObj.createUnlinkedContext([
                aDescriptor
        ])

        then:
        UnlinkedEntity<EntityA, EntityABuilder, EntityARelationships> unlinkedEntityA = (UnlinkedEntity<EntityA, EntityABuilder, EntityARelationships>) unlinkedContext.unlinkedEntities[aDescriptor]

        unlinkedEntityA.builder.newEntityBuilder().class.interfaces == [EntityABuilder]
        unlinkedEntityA.builder.newEntityBuilder().create().class.interfaces == [EntityA]
        unlinkedEntityA.builder.newEntityBuilder().withEntityAfield1("value1").create().getEntityAfield1() == "value1"

        when:
        unlinkedEntityA.persister.update(entityAMock)

        then:
        1 * mongoDaoMock.touch ("EntityA", aDbObjectMock)

        when:
        EntityDescriptor<EntityA, EntityABuilder, EntityARelationships> aDescriptorFromUnlinkedEntity = unlinkedEntityA.entityDescriptor

        then:
        aDescriptorFromUnlinkedEntity == aDescriptor

        when:
        unlinkedEntityA.retriever.byId(aObjectIdMock)

        then:
        1 * mongoDaoMock.findOne("EntityA", new BasicDBObject("_id", aObjectIdMock)) >> of(aDbObjectMock)
    }

}
