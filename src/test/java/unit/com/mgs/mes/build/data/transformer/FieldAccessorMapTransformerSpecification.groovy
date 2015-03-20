package com.mgs.mes.build.data.transformer

import com.mgs.reflection.FieldAccessor
import com.mgs.reflection.FieldAccessorType
import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import org.bson.types.ObjectId
import spock.lang.Specification

import static java.util.Optional.of

class FieldAccessorMapTransformerSpecification extends Specification {
    FieldAccessorMapTransformer testObj
    Map<FieldAccessor,Object> objectWithIdFieldValuesByAccessor = new HashMap<>()
    Map<FieldAccessor,Object> simpleFieldValuesByAccessor = new HashMap<>()
    Map<FieldAccessor,Object> complexFieldValuesByAccessor = new HashMap<>()
    FieldAccessor field1AccessorMock = Mock (FieldAccessor)
    FieldAccessor field2AccessorMock = Mock (FieldAccessor)
    FieldAccessor idAccessorMock = Mock (FieldAccessor)
    FieldAccessor childAccessorMock = Mock (FieldAccessor)
    Entity childEntityMock = Mock (Entity)
    DBObject childDboMock = Mock (DBObject)
    ObjectId objectIdMock = Mock (ObjectId)

    def "setup" (){
        testObj = new FieldAccessorMapTransformer()

        field1AccessorMock.fieldName >> "field1"
        field1AccessorMock.declaredType >> String
        field1AccessorMock.type >> FieldAccessorType.GET
        field1AccessorMock.methodName >> "getField1"

        field2AccessorMock.fieldName >> "field2"
        field2AccessorMock.declaredType >> String
        field2AccessorMock.type >> FieldAccessorType.GET
        field2AccessorMock.methodName >> "getField2"

        idAccessorMock.fieldName >> "id"
        idAccessorMock.declaredType >> ObjectId
        idAccessorMock.type >> FieldAccessorType.GET
        idAccessorMock.methodName >> "getId"

        simpleFieldValuesByAccessor.put(field1AccessorMock, "value1")
        simpleFieldValuesByAccessor.put(field2AccessorMock, "value2")

        objectWithIdFieldValuesByAccessor.put(field1AccessorMock, "value1")
        objectWithIdFieldValuesByAccessor.put(field2AccessorMock, "value2")
        objectWithIdFieldValuesByAccessor.put(idAccessorMock, of(objectIdMock))

        childAccessorMock.fieldName >> "child"
        childAccessorMock.declaredType >> Entity
        childAccessorMock.type >> FieldAccessorType.GET
        childAccessorMock.methodName >> "getChild"

        complexFieldValuesByAccessor.put(childAccessorMock, childEntityMock)
        childEntityMock.asDbo() >> childDboMock
    }

    def "should transform simple map" (){
        when:
        def result = testObj.transform(Entity, simpleFieldValuesByAccessor)

        then:
        result.dbo == new BasicDBObject().
                            append("field1", "value1").
                            append("field2", "value2")
        result.get("getField1") == "value1"
        result.get("getField2") == "value2"
    }

    def "should transform complex map" (){
        when:
        def result = testObj.transform(ComplexEntity, complexFieldValuesByAccessor)

        then:
        result.dbo == new BasicDBObject().
                append("child", childDboMock)
        result.get("getChild") == childEntityMock
    }

    def "should transform object with an Id" (){
        when:
        def result = testObj.transform(Entity, objectWithIdFieldValuesByAccessor)

        then:
        result.dbo == new BasicDBObject().
                append("field1", "value1").
                append("field2", "value2").
                append("_id", objectIdMock)
        result.get("getField1") == "value1"
        result.get("getField2") == "value2"
        result.get("getId") == of(objectIdMock)
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    static interface ComplexEntity extends com.mgs.mes.model.Entity {
        public Entity getChild ();
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    static interface Entity extends com.mgs.mes.model.Entity {
        public String getField1 ();
        public String getField2 ();
    }
}
