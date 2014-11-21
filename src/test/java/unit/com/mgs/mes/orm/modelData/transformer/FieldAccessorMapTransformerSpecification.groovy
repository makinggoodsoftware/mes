package com.mgs.mes.orm.modelData.transformer
import com.mgs.mes.model.MongoEntity
import com.mgs.reflection.FieldAccessor
import com.mgs.reflection.FieldAccessorType
import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import spock.lang.Specification

class FieldAccessorMapTransformerSpecification extends Specification {
    FieldAccessorMapTransformer testObj
    Map<FieldAccessor,Object> simpleFieldValuesByAccessor = new HashMap<>()
    Map<FieldAccessor,Object> complextFieldValuesByAccessor = new HashMap<>()
    FieldAccessor field1AccessorMock = Mock (FieldAccessor)
    FieldAccessor field2AccessorMock = Mock (FieldAccessor)
    FieldAccessor childAccessorMock = Mock (FieldAccessor)
    Entity childEntityMock = Mock (Entity)
    DBObject childDboMock = Mock (DBObject)

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

        simpleFieldValuesByAccessor.put(field1AccessorMock, "value1")
        simpleFieldValuesByAccessor.put(field2AccessorMock, "value2")

        childAccessorMock.fieldName >> "child"
        childAccessorMock.declaredType >> Entity
        childAccessorMock.type >> FieldAccessorType.GET
        childAccessorMock.methodName >> "getChild"

        complextFieldValuesByAccessor.put(childAccessorMock, childEntityMock)
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
        def result = testObj.transform(ComplexEntity, complextFieldValuesByAccessor)

        then:
        result.dbo == new BasicDBObject().
                append("child", childDboMock)
        result.get("getChild") == childEntityMock
    }

    static interface ComplexEntity extends MongoEntity {
        public Entity getChild ();
    }

    static interface Entity extends MongoEntity {
        public String getField1 ();
        public String getField2 ();
    }
}
