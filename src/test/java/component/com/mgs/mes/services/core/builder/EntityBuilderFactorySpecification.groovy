package com.mgs.mes.services.core.builder

import com.mgs.config.mes.entity.EntityConfig
import com.mgs.config.reflection.ReflectionConfig
import com.mgs.mes.model.Entity
import com.mgs.mes.model.EntityBuilder
import com.mgs.mes.model.OneToOne
import com.mgs.mes.services.core.reference.EntityReferenceProvider
import com.mgs.mes.simpleModel.entityA.EntityA
import com.mgs.mes.simpleModel.entityA.EntityABuilder
import com.mgs.mes.simpleModel.entityB.EntityB
import com.mgs.mes.simpleModel.entityB.EntityBBuilder
import com.mongodb.BasicDBObject
import org.bson.types.ObjectId
import spock.lang.Specification

import static java.util.Optional.empty
import static java.util.Optional.of

class EntityBuilderFactorySpecification extends Specification {
    EntityBuilderProvider<EntityB, EntityBBuilder> entityBBuilder
    EntityBuilderProvider<EntityA, EntityABuilder> entityABuilder
    EntityBuilderProvider<ReferenceEntity, ReferenceEntityBuilder> referenceEntityBuilder
    ReflectionConfig reflectionConfig = new ReflectionConfig()
    EntityConfig buildConfig = new EntityConfig(reflectionConfig)
    EntityReferenceProvider entityReferenceFactoryMock = Mock (EntityReferenceProvider)
    ObjectId objectIdMock = Mock (ObjectId)

    def "setup" (){
        entityBBuilder = buildConfig.factories().entityBuilder(EntityB, EntityBBuilder, entityReferenceFactoryMock)
        entityABuilder = buildConfig.factories().entityBuilder(EntityA, EntityABuilder, entityReferenceFactoryMock)
        referenceEntityBuilder = buildConfig.factories().entityBuilder(ReferenceEntity, ReferenceEntityBuilder, entityReferenceFactoryMock)
    }

    def "should create a simple model with partial values"(){
        when:
        def result = entityBBuilder.newEntity().withEntityBfield1("value1").create()

        then:
        result.asDbo() == new BasicDBObject("entityBfield1", "value1").append("entityBfield2", null).append("_id", null)
        result.entityBfield1 == "value1"
        result.entityBfield2 == null
        ! result.id.isPresent()
    }

    def "should create and update a simple model object"(){
        when:
        def result = entityBBuilder.newEntity().withEntityBfield1("value1").withEntityBfield2("value2").create()

        then:
        result.asDbo() == new BasicDBObject("entityBfield1", "value1").append("entityBfield2", "value2").append("_id", null)
        result.entityBfield1 == "value1"
        result.entityBfield2 == "value2"

        when:
        def updateResult = entityBBuilder.update(result).withEntityBfield2("value3").create()

        then:
        updateResult.asDbo() == new BasicDBObject("entityBfield1", "value1").append("entityBfield2", "value3").append("_id", null)
        updateResult.entityBfield1 == "value1"
        updateResult.entityBfield2 == "value3"
        ! updateResult.id.isPresent()
    }

    def "should create a complex model object"(){
        when:
        EntityB embedded = entityBBuilder.newEntity().
            withEntityBfield1("value2.1").
            withEntityBfield2("value2.2").
            create()
        def result = entityABuilder.newEntity().
                    withEntityAfield1("value1.1").
                    withEntityAfield2("value1.2").
                    withEmbedded(embedded).
                    create()

        then:
        result.asDbo() == new BasicDBObject().
                append("entityAfield1", "value1.1").
                append("entityAfield2", "value1.2").
                append("embedded",
                        new BasicDBObject().
                        append("entityBfield1", "value2.1").
                        append("entityBfield2", "value2.2").
                        append("_id", null)

                ).
                append("_id", null)

        result.entityAfield1 == "value1.1"
        result.entityAfield2 == "value1.2"
        result.embedded == embedded
        ! result.id.isPresent()
    }

    def "should create an entity that refers another entity" (){
        given:
        OneToOne<EntityA> entityAReferenceMock = Mock (OneToOne)
        entityAReferenceMock.refId >> objectIdMock
        entityAReferenceMock.refName >> "EntityA"
        entityAReferenceMock.asDbo() >> new BasicDBObject().
                append("refName", "EntityA").
                append("refId", objectIdMock)
        EntityA entityAMock = Mock (EntityA)
        entityAMock.id >> of(objectIdMock)

        entityReferenceFactoryMock.newReference(entityAMock) >> entityAReferenceMock

        when:
        ReferenceEntity result = referenceEntityBuilder.newEntity()
            .withRelationship(entityAMock)
            .create()

        then:
        result.asDbo() == new BasicDBObject().
                append("relationship", new BasicDBObject().
                    append("refName", "EntityA").
                    append("refId", objectIdMock)
                ).
                append("_id", null)

        result.relationship.refName == "EntityA"
        result.relationship.refId == objectIdMock
        result.id == empty()
    }

    public static interface ReferenceEntity extends Entity{
        public OneToOne<EntityA> getRelationship ()
    }

    public static interface ReferenceEntityBuilder extends EntityBuilder<ReferenceEntity>{
        public ReferenceEntityBuilder withRelationship (EntityA entityA)
    }
}