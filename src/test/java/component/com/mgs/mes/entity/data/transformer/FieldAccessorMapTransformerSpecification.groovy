package com.mgs.mes.entity.data.transformer

import com.mgs.config.reflection.ReflectionConfig
import com.mgs.mes.entity.data.EntityData
import com.mgs.mes.model.Entity
import com.mgs.mes.model.OneToMany
import com.mgs.mes.model.OneToOne
import com.mgs.reflection.FieldAccessor
import com.mgs.reflection.FieldAccessorParser
import com.mgs.reflection.Reflections
import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import org.bson.types.ObjectId
import spock.lang.Specification

class FieldAccessorMapTransformerSpecification extends Specification {
    ReflectionConfig reflectionConfig = new ReflectionConfig();
    FieldAccessorMapTransformer testObj
    FieldAccessorParser fieldAccessorParser = reflectionConfig.fieldAccessorParser()

    Map<FieldAccessor, Object> valuesByAccessor

    SimpleEntity simpleEntityMock = Mock (SimpleEntity)
    DBObject simpleEntityDbObjectMock = Mock (DBObject)

    OneToOne oneToOneEntityMock = Mock(OneToOne)
    DBObject oneToOneDbObjectMock = Mock (DBObject)

    OneToMany oneToManyEntityMock = Mock(OneToMany)
    DBObject oneToManyDbObjectMock = Mock (DBObject)

    ObjectId objectIdMock = Mock (ObjectId)

    def "setup"() {
        testObj = new FieldAccessorMapTransformer(new Reflections())
        valuesByAccessor = new HashMap<FieldAccessor, Object>()
        simpleEntityMock.asDbo() >> simpleEntityDbObjectMock
        oneToOneEntityMock.asDbo() >> oneToOneDbObjectMock
        oneToManyEntityMock.asDbo() >> oneToManyDbObjectMock
    }

    def "should transform simple entities"() {
        given:
        valuesByAccessor.put(
                fieldAccessorParser.parse(SimpleEntity, "getField1").get(),
                "value1"
        )
        valuesByAccessor.put(
                fieldAccessorParser.parse(SimpleEntity, "getField2").get(),
                "value2"
        )

        when:
        EntityData result = testObj.transform(SimpleEntity, valuesByAccessor)

        then:
        result.dbo == new BasicDBObject().
                append("field1", "value1").
                append("field2", "value2")
        result.get("getField1") == "value1"
        result.get("getField2") == "value2"
    }

    def "should transform embedded entities" (){
        given:
        valuesByAccessor.put(
                fieldAccessorParser.parse(ComplexEntity, "getChild").get(),
                simpleEntityMock
        )

        when:
        def result = testObj.transform(ComplexEntity, valuesByAccessor)

        then:
        result.dbo == new BasicDBObject().
                append("child", simpleEntityDbObjectMock)
        result.get("getChild") == simpleEntityMock
    }

    def "should transform one to one entities" (){
        given:
        valuesByAccessor.put(
                fieldAccessorParser.parse(RelationshipEntity, "getRelationship").get(),
                oneToOneEntityMock
        )

        when:
        def result = testObj.transform(RelationshipEntity, valuesByAccessor)

        then:
        result.dbo == new BasicDBObject().
                append("relationship", oneToOneDbObjectMock)
        result.get("getRelationship") == oneToOneEntityMock
    }

    def "should transform simple list entities" (){
        given:
        valuesByAccessor.put(
                fieldAccessorParser.parse(SimpleListEntity, "getStrings").get(),
                ['1','2','3']
        )

        when:
        def result = testObj.transform(RelationshipEntity, valuesByAccessor)

        then:
        result.dbo == new BasicDBObject().
                append("strings", ['1','2','3'])
        result.get("getStrings") == ['1','2','3']
    }

    def "should transform complex list entities" (){
        given:
        valuesByAccessor.put(
                fieldAccessorParser.parse(ComplexListEntity, "getEntities").get(),
                [simpleEntityMock]
        )

        when:
        def result = testObj.transform(ComplexListEntity, valuesByAccessor)

        then:
        result.dbo == new BasicDBObject().
                append("entities", [simpleEntityDbObjectMock])
        result.get("getEntities") == [simpleEntityMock]
    }

    def "should transform OneToMany entities" (){
        given:
        valuesByAccessor.put(
                fieldAccessorParser.parse(OneToManyEntity, "getRelationship").get(),
                [oneToManyEntityMock]
        )

        when:
        EntityData result = testObj.transform(OneToManyEntity, valuesByAccessor)

        then:
        result.dbo == new BasicDBObject().
                append("relationship", [oneToManyDbObjectMock])
        result.get("getRelationship") == [oneToManyEntityMock]

    }

    def "should transform the Id" (){
        given:
        valuesByAccessor.put(
                fieldAccessorParser.parse(SimpleEntity, "getField1").get(),
                "value1"
        )
        valuesByAccessor.put(
                fieldAccessorParser.parse(SimpleEntity, "getField2").get(),
                "value2"
        )
        valuesByAccessor.put(
                fieldAccessorParser.parse(SimpleEntity, "getId").get(),
                Optional.of(objectIdMock)
        )

        when:
        def result = testObj.transform(SimpleEntity, valuesByAccessor)

        then:
        result.dbo == new BasicDBObject().
                append("field1", "value1").
                append("field2", "value2").
                append("_id", objectIdMock)
        result.get("getField1") == "value1"
        result.get("getField2") == "value2"
        result.get("getId") == Optional.of(objectIdMock)
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    static interface ComplexListEntity extends Entity {
        public List<SimpleEntity> getEntities();
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    static interface SimpleListEntity extends Entity {
        public List<String> getStrings();
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    static interface ComplexEntity extends Entity {
        public SimpleEntity getChild();
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    static interface SimpleEntity extends Entity {
        public String getField1();
        public String getField2();
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    static interface RelationshipEntity extends Entity {
        public OneToOne<SimpleEntity> getRelationship();
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    static interface OneToManyEntity extends Entity {
        public OneToMany<SimpleEntity> getRelationship();
    }

}
