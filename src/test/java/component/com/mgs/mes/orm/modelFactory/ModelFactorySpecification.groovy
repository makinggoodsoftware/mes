package com.mgs.mes.orm.modelFactory

import com.mgs.mes.model.MongoEntity
import com.mgs.mes.orm.modelData.ModelDataFactory
import com.mgs.mes.orm.modelData.transformer.DboTransformer
import com.mgs.mes.orm.modelData.transformer.FieldAccessorMapTransformer
import com.mgs.reflection.BeanNamingExpert
import com.mgs.reflection.FieldAccessorParser
import com.mongodb.BasicDBObject
import spock.lang.Specification

class ModelFactorySpecification extends Specification {
    ModelFactory testObj
    DynamicModelFactory dynamicModelFactory = new DynamicModelFactory()
    BeanNamingExpert beanNamingExpert = new BeanNamingExpert()
    FieldAccessorParser fieldAccessorParser = new FieldAccessorParser(beanNamingExpert)
    ModelDataFactory modelDataFactory = new ModelDataFactory(
            new DboTransformer(dynamicModelFactory, beanNamingExpert, fieldAccessorParser),
            new FieldAccessorMapTransformer()
    )

    def "setup" (){
        testObj = new ModelFactory(dynamicModelFactory, modelDataFactory)
    }

    def "should create object from simple dbo" (){
        given:
        BasicDBObject dbObject = new BasicDBObject().
                append("field1", "hello world").
                append("field2", 2).
                append("field3", 3)

        when:
        def result = testObj.from(SimpleEntity.class, dbObject)

        then:
        result.asDbo().is(dbObject)
        result.getField1() == "hello world"
        result.getField2() == 2
        result.getField3() == 3
    }

    def "should create object from nested dbo" (){
        given:
        BasicDBObject simpleDbObject = new BasicDBObject().
                append("field1", "hello world").
                append("field2", 2).
                append("field3", 3)

        BasicDBObject nestedDbObject = new BasicDBObject().
                append("field4", "goodbye world").
                append("field5", 4).
                append("field6", 5).
                append("child", simpleDbObject)


        when:
        def result = testObj.from(NestedEntity.class, nestedDbObject)

        then:
        result.asDbo().is(nestedDbObject)
        result.getField4() == "goodbye world"
        result.getField5() == 4
        result.getField6() == 5
        result.getChild ().asDbo().is (simpleDbObject)
        result.getChild ().getField1() == "hello world"
        result.getChild ().getField2() == 2
        result.getChild ().getField3() == 3
    }

    public static interface NestedEntity extends MongoEntity {
        public String getField4();
        public int getField5();
        public Integer getField6();
        public SimpleEntity getChild();
    }


    public static interface SimpleEntity extends MongoEntity {
        public String getField1();
        public int getField2();
        public Integer getField3();
    }
}
