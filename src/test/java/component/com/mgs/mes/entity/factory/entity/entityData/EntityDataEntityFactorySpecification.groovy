package com.mgs.mes.entity.factory.entity.entityData

import com.mgs.config.mes.entity.EntityConfig
import com.mgs.config.reflection.ReflectionConfig
import com.mgs.mes.entity.data.EntityData
import com.mgs.mes.entity.factory.entity.EntityFactory
import com.mgs.mes.model.Entity
import com.mongodb.DBObject
import org.bson.types.ObjectId
import spock.lang.Specification

import static java.util.Optional.empty
import static java.util.Optional.of

class EntityDataEntityFactorySpecification extends Specification {
    EntityFactory<EntityData> testObj
    ReflectionConfig reflectionConfig = new ReflectionConfig()
    EntityConfig buildConfig = new EntityConfig(reflectionConfig)

    EntityData simpleEntityData = Mock(EntityData)
    DBObject simpleDboMock = Mock(DBObject)

    EntityData complexEntityData = Mock(EntityData)
    DBObject complexDboMock = Mock(DBObject)
    ObjectId complexId = Mock (ObjectId)

    SimpleEntity childEntityMock = Mock(SimpleEntity)

    def "setup" (){
        testObj = buildConfig.factories().entityDataEntity()

        simpleEntityData.get("getValue") >> "value1"
        simpleEntityData.get("getId") >> empty()
        simpleEntityData.dbo >> simpleDboMock

        complexEntityData.get("getBlah") >> "value2"
        complexEntityData.get("getChild") >> childEntityMock
        complexEntityData.get("getId") >> of(complexId)
        complexEntityData.dbo >> complexDboMock
    }

    def "should create entity from simple entity data" (){
        when:
        SimpleEntity result = testObj.from(SimpleEntity, simpleEntityData)

        then:
        result.id == empty()
        result.value == "value1"
        result.asDbo() == simpleDboMock
    }

    def "should create entity from complex entity data" (){
        when:
        ComplexEntity result = testObj.from(ComplexEntity, complexEntityData)

        then:
        result.id == of(complexId)
        result.blah == "value2"
        result.child.is(childEntityMock)
        result.asDbo() == complexDboMock
    }

    private static interface SimpleEntity extends Entity{
        String getValue ();
    }

    private static interface ComplexEntity extends Entity{
        String getBlah ();
        SimpleEntity getChild ();
    }
}