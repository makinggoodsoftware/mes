package com.mgs.mes.entity.data.transformer

import com.mgs.mes.entity.data.EntityData
import com.mgs.mes.entity.factory.entity.entityData.EntityDataEntityFactory
import com.mgs.mes.model.Entity
import com.mgs.mes.model.EntityReference
import com.mgs.reflection.BeanNamingExpert
import com.mgs.reflection.FieldAccessor
import com.mgs.reflection.FieldAccessorParser
import com.mgs.reflection.Reflections
import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import org.bson.types.ObjectId
import spock.lang.Specification

import static com.mgs.reflection.FieldAccessorType.GET
import static java.util.Optional.empty
import static java.util.Optional.of

class DboTransformerSpecification extends Specification {
    DboTransformer testObj
    FieldAccessorParser fieldAccessorParserMock = Mock (FieldAccessorParser)
    BeanNamingExpert beanNamingExpertMock = Mock (BeanNamingExpert)
    EntityDataEntityFactory entityFactoryMock = Mock (EntityDataEntityFactory)
    SimpleEntity entityMock = Mock (SimpleEntity)
    ObjectId objectIdMock = Mock (ObjectId)
    EntityReference<SimpleEntity> referenceMock = Mock(EntityReference)
    Reflections reflectionsMock = Mock (Reflections)

    def "setup" (){
        FieldAccessor field1Accessor = new FieldAccessor(String, "getField1", "field1", "get", GET)
        FieldAccessor field2Accessor = new FieldAccessor(String, "getField2", "field2", "get", GET)
        FieldAccessor childAccessor = new FieldAccessor(SimpleEntity, "getChild", "child", "get", GET)
        FieldAccessor relationshipAccessor = new FieldAccessor(EntityReference, "getRelationship", "relationship", "get", GET)

        fieldAccessorParserMock.parse(SimpleEntity) >> [field1Accessor, field2Accessor].stream()
        fieldAccessorParserMock.parse(SimpleEntity, "getField1") >> of(field1Accessor)
        fieldAccessorParserMock.parse(SimpleEntity, "getField2") >> of(field2Accessor)

        fieldAccessorParserMock.parse(ComplexEntity) >> [childAccessor].stream()
        fieldAccessorParserMock.parse(ComplexEntity, "getChild") >> of(childAccessor)

        fieldAccessorParserMock.parse(RelationshipEntity) >> [relationshipAccessor].stream()
        fieldAccessorParserMock.parse(RelationshipEntity, "getRelationship") >> of(relationshipAccessor)

        beanNamingExpertMock.getGetterName("field1") >> "getField1"
        beanNamingExpertMock.getGetterName("field2") >> "getField2"
        beanNamingExpertMock.getGetterName("child") >> "getChild"
        beanNamingExpertMock.getGetterName("relationship") >> "getRelationship"
        beanNamingExpertMock.getGetterName("refName") >> "getRefName"
        beanNamingExpertMock.getGetterName("refId") >> "getRefId"

        testObj = new DboTransformer(entityFactoryMock, beanNamingExpertMock, fieldAccessorParserMock, reflectionsMock)
        reflectionsMock.isAssignableTo(BasicDBObject, List) >> false
        reflectionsMock.isAssignableTo(BasicDBObject, DBObject) >> true
    }

    def "should ignore field is there is not matching getter" (){
        given:
        def dbo = new BasicDBObject().
                append("blah", "value1")

        when:
        def result = testObj.transform(SimpleEntity, dbo)

        then:
        result.dbo.is(dbo)
        ! result.exists("getBlah")
    }

    def "should transform partial" (){
        given:
        def dbo = new BasicDBObject().
                append("field1", "value1")

        when:
        def result = testObj.transform(SimpleEntity, dbo)

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
        def result = testObj.transform(SimpleEntity, dbo)

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
        def result = testObj.transform(SimpleEntity, dbo)

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
        entityFactoryMock.from (_, _) >> {type, entityData ->
            if (type != SimpleEntity) throw new IllegalArgumentException("Shouldn't get call with a type different than Entity for this test cases")
            if (
                (entityData.dbo.get("field1") == "value1") &&
                (entityData.dbo.get("field2") == "value2") &&
                (entityData.get("getField1") == "value1") &&
                (entityData.get("getField2") == "value2")
            ) {
                return entityMock
            } else {
                throw new RuntimeException("Unexpected model entityData received " + entityData)
            }
        }

        when:
        def result = testObj.transform(ComplexEntity, complexDbo)

        then:
        result.dbo.is(complexDbo)
        result.get("getChild").is(entityMock)
        result.get("getId") == of(objectIdMock)
    }

    def "should transfrom a relationship dbo" (){
        given:
        def referencedDbo = new BasicDBObject().
                append("refName", "SimpleEntity").
                append("refId", objectIdMock).
                append("_id", null)
        def relationshipDbo = new BasicDBObject().
                append("relationship", referencedDbo)
        entityFactoryMock.from(EntityReference, new EntityData(referencedDbo, [
                getRefName : 'SimpleEntity',
                getRefId : objectIdMock,
                getId   : empty()
        ])) >> referenceMock

        when:
        def result = testObj.transform(RelationshipEntity, relationshipDbo)

        then:
        result.dbo.is(relationshipDbo)
        result.get("getRelationship") == referenceMock
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
        testObj.transform(SimpleEntity, dbo)

        then:
        thrown IllegalArgumentException
    }


    @SuppressWarnings("GroovyUnusedDeclaration")
    public static interface ComplexEntity extends Entity {
        public SimpleEntity getChild();
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    public static interface SimpleEntity extends Entity {
        public String getField1();
        public String getField2();
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    static interface RelationshipEntity extends Entity {
        public EntityReference<SimpleEntity> getRelationship ();
    }

}
