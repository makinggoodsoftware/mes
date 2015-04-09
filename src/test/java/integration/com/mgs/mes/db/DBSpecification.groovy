package com.mgs.mes.db

import com.mgs.config.mes.build.BuildConfig
import com.mgs.config.mes.context.ContextConfig
import com.mgs.config.mes.db.DatabaseConfig
import com.mgs.config.mes.meta.MetaConfig
import com.mgs.config.reflection.ReflectionConfig
import com.mgs.mes.context.EntityDescriptor
import com.mgs.mes.context.unlinkedContext.UnlinkedEntity
import com.mgs.mes.context.unlinkedContext.UnlinkedMongoContext
import com.mgs.mes.context.unlinkedContext.UnlinkedMongoContextFactory
import com.mgs.mes.simpleModel.entityB.EntityB
import com.mgs.mes.simpleModel.entityB.EntityBBuilder
import com.mgs.mes.simpleModel.entityB.EntityBRelationships
import spock.lang.Specification

class DBSpecification extends Specification {
    UnlinkedEntity<EntityB, EntityBBuilder, EntityBRelationships> Bs
    DatabaseConfig databaseConfig = new DatabaseConfig();
    ReflectionConfig reflectionConfig = new ReflectionConfig()
    ContextConfig contextConfig = new ContextConfig(
            new MetaConfig(reflectionConfig),
            new BuildConfig(reflectionConfig),
            reflectionConfig
    )

    def "setup" (){
        MongoDao dao = databaseConfig.dao("localhost", 27017, "testDb")
        final UnlinkedMongoContextFactory orchestrator = contextConfig.unlinkedMongoContextFactory(dao)
        EntityDescriptor<EntityB, EntityBBuilder, EntityBRelationships> descriptor = new EntityDescriptor<>(EntityB, EntityBBuilder, EntityBRelationships)
        UnlinkedMongoContext context = orchestrator.createUnlinkedContext([descriptor])
        Bs = context.unlinkedEntities[descriptor]
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
