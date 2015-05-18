package com.mgs.mes.entity.data.transformer

import com.mgs.mes.entity.factory.entity.entityData.EntityDataEntityFactory
import com.mgs.mes.model.Entity
import com.mgs.mes.model.OneToMany
import com.mgs.mes.model.OneToOne
import com.mgs.reflection.BeanNamingExpert
import com.mgs.reflection.FieldAccessorParser
import com.mgs.reflection.Reflections
import com.mongodb.BasicDBObject
import org.bson.types.ObjectId
import spock.lang.Specification

import static java.util.Optional.empty
import static java.util.Optional.of

class DboTransformerSpecification extends Specification {
    DboTransformer testObj
    ObjectId objectIdMock = Mock (ObjectId)

    def "setup" (){
        BeanNamingExpert beanNamingExpert = new BeanNamingExpert()
        testObj = new DboTransformer(
                new EntityDataEntityFactory(),
                beanNamingExpert,
                new FieldAccessorParser(beanNamingExpert, genericsExpert),
                new Reflections())
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

        when:
        def result = testObj.transform(ComplexEntity, complexDbo)

        then:
        result.dbo.is(complexDbo)
        result.get("getChild").getField1() == 'value1'
        result.get("getChild").getField2() == 'value2'
        result.get("getId") == of(objectIdMock)
    }

    def "should transfrom a complex list of entities" (){

    }

    def "should transform a one to one relationship dbo" (){
        given:
        def referencedDbo = new BasicDBObject().
                append("refName", "SimpleEntity").
                append("refId", objectIdMock).
                append("_id", null)
        def relationshipDbo = new BasicDBObject().
                append("relationship", referencedDbo)

        when:
        def result = testObj.transform(RelationshipEntity, relationshipDbo)

        then:
        result.dbo.is(relationshipDbo)
        result.get("getRelationship").getRefName() == 'SimpleEntity'
        result.get("getRelationship").getRefId() == objectIdMock
    }

    def "should transform a one to many relationship dbo" (){
        given:
        def referencedDbo = new BasicDBObject().
                append("refName", "SimpleEntity").
                append("refId", objectIdMock).
                append("_id", null)
        def relationshipDbo = new BasicDBObject().
                append("relationship", [referencedDbo])

        when:
        def result = testObj.transform(OneToManyEntity, relationshipDbo)

        then:
        result.dbo.is(relationshipDbo)
        result.get("getRelationship").getList().size() == 1
        result.get("getRelationship").getList().get(0).getRefName() == 'SimpleEntity'
        result.get("getRelationship").getList().get(0).getRefId() == objectIdMock
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
        public OneToOne<SimpleEntity> getRelationship ();
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    static interface OneToManyEntity extends Entity {
        public OneToMany<SimpleEntity> getRelationship ();
    }

}
