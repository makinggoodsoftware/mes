package com.mgs.mes.orm.modelBuilder

import com.mgs.mes.EntityA
import com.mgs.mes.EntityABuilder
import com.mgs.mes.EntityB
import com.mgs.mes.EntityBBuilder
import com.mgs.mes.orm.modelData.ModelDataBuilderFactory
import com.mgs.mes.orm.modelData.ModelDataFactory
import com.mgs.mes.orm.modelData.transformer.DboTransformer
import com.mgs.mes.orm.modelData.transformer.FieldAccessorMapTransformer
import com.mgs.mes.orm.modelFactory.DynamicModelFactory
import com.mgs.reflection.BeanNamingExpert
import com.mgs.reflection.FieldAccessorParser
import com.mongodb.BasicDBObject
import spock.lang.Specification

class ModelBuilderFactorySpecification extends Specification {
    ModelBuilderFactory<EntityB, EntityBBuilder> entityBBuilder
    ModelBuilderFactory<EntityA, EntityABuilder> entityABuilder
    BeanNamingExpert beanNamingExpert = new BeanNamingExpert()
    FieldAccessorParser fieldAccessorParser = new FieldAccessorParser(beanNamingExpert)
    ModelDataFactory modelDataFactory = new ModelDataFactory(
            new DboTransformer(
                new DynamicModelFactory(), beanNamingExpert, fieldAccessorParser
            ), new FieldAccessorMapTransformer()
    )
    ModelDataBuilderFactory modelDataBuilderFactory = new ModelDataBuilderFactory(modelDataFactory, beanNamingExpert, fieldAccessorParser)

    def "setup" (){
        entityBBuilder = new ModelBuilderFactory(modelDataBuilderFactory, fieldAccessorParser, beanNamingExpert, EntityB, EntityBBuilder, new DynamicModelFactory())
        entityABuilder = new ModelBuilderFactory(modelDataBuilderFactory, fieldAccessorParser, beanNamingExpert, EntityA, EntityABuilder, new DynamicModelFactory())
    }

    def "should create a simple model with partial values"(){
        when:
        def result = entityBBuilder.newEntityBuilder().withEntityBfield1("value1").create()

        then:
        result.asDbo() == new BasicDBObject("entityBfield1", "value1").append("entityBfield2", null)
        result.entityBfield1 == "value1"
        result.entityBfield2 == null
    }

    def "should create and update a simple model object"(){
        when:
        def result = entityBBuilder.newEntityBuilder().withEntityBfield1("value1").withEntityBfield2("value2").create()

        then:
        result.asDbo() == new BasicDBObject("entityBfield1", "value1").append("entityBfield2", "value2")
        result.entityBfield1 == "value1"
        result.entityBfield2 == "value2"

        when:
        def updateResult = entityBBuilder.newEntityBuilderFrom(result).withEntityBfield2("value3").create()

        then:
        updateResult.asDbo() == new BasicDBObject("entityBfield1", "value1").append("entityBfield2", "value3")
        updateResult.entityBfield1 == "value1"
        updateResult.entityBfield2 == "value3"
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
                        append("entityBfield2", "value2.2")
                )

        result.entityAfield1 == "value1.1"
        result.entityAfield2 == "value1.2"
        result.embedded == embedded
    }
}
