package com.mgs.mes.entity.factory.entity.dbo

import com.mgs.config.mes.entity.EntityConfig
import com.mgs.config.reflection.ReflectionConfig
import com.mgs.mes.entity.factory.entity.EntityFactory
import com.mgs.mes.model.Entity
import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import spock.lang.Specification

import static java.util.Optional.empty

class DBObjectEntityFactorySpecification extends Specification {
    EntityFactory<DBObject> testObj
    ReflectionConfig reflectionConfig = new ReflectionConfig()
    EntityConfig buildConfig = new EntityConfig(reflectionConfig)

    def "setup" (){
        testObj = buildConfig.factories().dbObjectEntity()
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
        result.getId() == empty()
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
        result.getId() == empty()
        result.getChild ().asDbo().is (simpleDbObject)
        result.getChild ().getField1() == "hello world"
        result.getChild ().getField2() == 2
        result.getChild ().getField3() == 3
        result.getChild ().getId() == empty()
    }

    def "should be equals" (){
        given: "Two different dbo dbos with the same entityData"
        BasicDBObject left = new BasicDBObject().
                append("field1", "hello world").
                append("field2", 2).
                append("field3", 3)
        BasicDBObject rigth = new BasicDBObject().
                append("field2", 2).
                append("field3", 3).
                append("field1", "hello world")

        when: "Comparing the resultant model objects"
        def leftModel = testObj.from(SimpleEntity, left)
        def rightModel = testObj.from(SimpleEntity, rigth)
        def result = leftModel.equals(rightModel)

        then: "Should be considered equals"
        result
    }

    public static interface NestedEntity extends Entity {
        public String getField4();
        public int getField5();
        public Integer getField6();
        public SimpleEntity getChild();
    }


    public static interface SimpleEntity extends Entity {
        public String getField1();
        public int getField2();
        public Integer getField3();
    }
}
