package com.mgs.mes.build.data.transformer

import com.mgs.mes.build.factory.entity.entityData.EntityDataEntityFactory
import com.mgs.reflection.BeanNamingExpert
import com.mgs.reflection.FieldAccessor
import com.mgs.reflection.FieldAccessorParser
import com.mongodb.BasicDBObject
import org.bson.types.ObjectId
import spock.lang.Specification

import static com.mgs.reflection.FieldAccessorType.GET
import static java.util.Optional.empty
import static java.util.Optional.of

class DboTransformerSpecification extends Specification {
    DboTransformer testObj
    FieldAccessorParser fieldAccessorParserMock = Mock (FieldAccessorParser)
    BeanNamingExpert beanNamingExpertMock = Mock (BeanNamingExpert)
    EntityDataEntityFactory dynamicDataModelMock = Mock (EntityDataEntityFactory)
    Entity entityMock = Mock (Entity)
    ObjectId objectIdMock = Mock (ObjectId)

    def "setup" (){
        FieldAccessor field1Accessor = new FieldAccessor(Entity, "getField1", "field1", "get", GET)
        FieldAccessor field2Accessor = new FieldAccessor(Entity, "getField2", "field2", "get", GET)
        FieldAccessor childAccessor = new FieldAccessor(Entity, "getChild", "child", "get", GET)

        fieldAccessorParserMock.parse(Entity) >> [field1Accessor, field2Accessor].stream()
        fieldAccessorParserMock.parse(Entity, "getField1") >> of(field1Accessor)
        fieldAccessorParserMock.parse(Entity, "getField2") >> of(field2Accessor)

        fieldAccessorParserMock.parse(ComplexEntity) >> [childAccessor].stream()
        fieldAccessorParserMock.parse(ComplexEntity, "getChild") >> of(childAccessor)

        beanNamingExpertMock.getGetterName("field1") >> "getField1"
        beanNamingExpertMock.getGetterName("field2") >> "getField2"
        beanNamingExpertMock.getGetterName("child") >> "getChild"

        testObj = new DboTransformer(dynamicDataModelMock, beanNamingExpertMock, fieldAccessorParserMock)
    }

    def "should ignore field is there is not matching getter" (){
        given:
        def dbo = new BasicDBObject().
                append("blah", "value1")

        when:
        def result = testObj.transform(Entity, dbo)

        then:
        result.dbo.is(dbo)
        ! result.exists("getBlah")
    }

    def "should transform partial" (){
        given:
        def dbo = new BasicDBObject().
                append("field1", "value1")

        when:
        def result = testObj.transform(Entity, dbo)

        then:
        result.dbo.is(dbo)
        result.get("getField1") == "value1"
        result.get("getField2") == null
    }

    def "should transform simple dbo" (){
        given:
        def dbo = new BasicDBObject().
                    append("field1", "value1").
                    append("field2", "value2")

        when:
        def result = testObj.transform(Entity, dbo)

        then:
        result.dbo.is(dbo)
        result.get("getField1") == "value1"
        result.get("getField2") == "value2"
        result.get("getId") == empty()
    }

    def "should transform object with id" (){
        given:
        def dbo = new BasicDBObject().
                append("field1", "value1").
                append("field2", "value2").
                append("_id", objectIdMock)

        when:
        def result = testObj.transform(Entity, dbo)

        then:
        result.dbo.is(dbo)
        result.get("getField1") == "value1"
        result.get("getField2") == "value2"
        result.get("getId") == of(objectIdMock)
    }

    def "should transform complex dbo" (){
        given:
        def simpleDbo = new BasicDBObject().
                append("field1", "value1").
                append("field2", "value2").
                append("_id", null)
        def complexDbo = new BasicDBObject().
                append("child", simpleDbo).
                append("_id", objectIdMock)

        //noinspection GroovyAssignabilityCheck
        dynamicDataModelMock.from (_, _) >> {type, modelData ->
            if (type != Entity) throw new IllegalArgumentException("Shouldn't get call with a type different than Entity for this test cases")
            if (
                (modelData.dbo.get("field1") == "value1") &&
                (modelData.dbo.get("field2") == "value2") &&
                (modelData.get("getField1") == "value1") &&
                (modelData.get("getField2") == "value2")
            ) {
                return entityMock
            } else {
                throw new RuntimeException("Unexpected model entityData received " + modelData)
            }
        }

        when:
        def result = testObj.transform(ComplexEntity, complexDbo)

        then:
        result.dbo.is(complexDbo)
        result.get("getChild").is(entityMock)
        result.get("getId") == of(objectIdMock)
    }

    def "should throw an exception if inner object has an ID" (){
        given:
        def simpleDbo = new BasicDBObject().
                append("field1", "value1").
                append("field2", "value2").
                append("_id", objectIdMock)
        def complexDbo = new BasicDBObject().
                append("child", simpleDbo).
                append("_id", objectIdMock)

        when:
        testObj.transform(ComplexEntity, complexDbo)

        then:
        thrown IllegalArgumentException
    }

    def "should throw an exception if there is a dbo field called id" (){
        given:
        def dbo = new BasicDBObject().
                append("id", "value1")

        when:
        testObj.transform(Entity, dbo)

        then:
        thrown IllegalArgumentException
    }



    public static interface ComplexEntity extends com.mgs.mes.model.Entity {
        public Entity getChild();
    }

    public static interface Entity extends com.mgs.mes.model.Entity {
        public String getField1();
        public String getField2();
    }

}
