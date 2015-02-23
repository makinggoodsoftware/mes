package com.mgs.mes.model.builder
import com.mgs.mes.entityA.EntityA
import com.mgs.mes.entityA.EntityABuilder
import com.mgs.mes.entityB.EntityB
import com.mgs.mes.entityB.EntityBBuilder
import com.mgs.mes.model.data.ModelDataBuilderFactory
import com.mgs.mes.model.data.ModelDataFactory
import com.mgs.mes.model.data.transformer.DboTransformer
import com.mgs.mes.model.data.transformer.FieldAccessorMapTransformer
import com.mgs.mes.model.factory.modelData.ModelDataModelFactory
import com.mgs.reflection.BeanNamingExpert
import com.mgs.reflection.FieldAccessorParser
import com.mongodb.BasicDBObject
import spock.lang.Specification

class ModelBuilderFactorySpecification extends Specification {
    EntityBuilderFactory<EntityB, EntityBBuilder> entityBBuilder
    EntityBuilderFactory<EntityA, EntityABuilder> entityABuilder
    BeanNamingExpert beanNamingExpert = new BeanNamingExpert()
    FieldAccessorParser fieldAccessorParser = new FieldAccessorParser(beanNamingExpert)
    ModelDataFactory modelDataFactory = new ModelDataFactory(
            new DboTransformer(
                new ModelDataModelFactory(), beanNamingExpert, fieldAccessorParser
            ), new FieldAccessorMapTransformer()
    )
    ModelDataBuilderFactory modelDataBuilderFactory = new ModelDataBuilderFactory(modelDataFactory, beanNamingExpert, fieldAccessorParser)

    def "setup" (){
        entityBBuilder = new EntityBuilderFactory(modelDataBuilderFactory, fieldAccessorParser, beanNamingExpert, new ModelDataModelFactory(), EntityB, EntityBBuilder)
        entityABuilder = new EntityBuilderFactory(modelDataBuilderFactory, fieldAccessorParser, beanNamingExpert, new ModelDataModelFactory(), EntityA, EntityABuilder)
    }

    def "should create a simple model with partial values"(){
        when:
        def result = entityBBuilder.newEntityBuilder().withEntityBfield1("value1").create()

        then:
        result.asDbo() == new BasicDBObject("entityBfield1", "value1").append("entityBfield2", null).append("_id", null)
        result.entityBfield1 == "value1"
        result.entityBfield2 == null
        ! result.id.isPresent()
    }

    def "should create and update a simple model object"(){
        when:
        def result = entityBBuilder.newEntityBuilder().withEntityBfield1("value1").withEntityBfield2("value2").create()

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
        EntityB embedded = entityBBuilder.newEntityBuilder().
            withEntityBfield1("value2.1").
            withEntityBfield2("value2.2").
            create()
        def result = entityABuilder.newEntityBuilder().
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
}
