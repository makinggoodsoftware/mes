package com.mgs.mes.v2.entity

import com.mgs.mes.model.Entity
import com.mgs.mes.model.OneToMany
import com.mgs.mes.model.OneToOne
import com.mgs.mes.v2.entity.property.descriptor.DomainPropertyDescriptor
import com.mgs.mes.v2.entity.property.descriptor.DomainPropertyDescriptorRetriever
import com.mgs.mes.v2.entity.property.manager.DomainPropertyManager
import com.mgs.mes.v2.entity.property.type.dbo.DboPropertyType
import com.mgs.mes.v2.entity.property.type.domain.DomainPropertyType
import com.mgs.mes.v2.polymorphism.PolymorphismManager
import com.mgs.reflection.BeanNamingExpert
import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import org.bson.types.ObjectId
import spock.lang.Specification

class EntityFactorySpecification extends Specification {
    EntityFactory testObj

    EntityProxyFactory entityProxyFactoryMock = Mock (EntityProxyFactory)
    DomainPropertyDescriptorRetriever domainPropertyDescriptorRetrieverMock = Mock (DomainPropertyDescriptorRetriever)
    PolymorphismManager polymorphismManagerMock = Mock (PolymorphismManager)
    BeanNamingExpert beanNamingExpertMock = Mock (BeanNamingExpert)
    ObjectId objectId1Mock = Mock (ObjectId)
    ObjectId objectId2Mock = Mock (ObjectId)

    def "setup" (){
        testObj = new EntityFactory(
                entityProxyFactoryMock,
                domainPropertyDescriptorRetrieverMock,
                polymorphismManagerMock,
                beanNamingExpertMock, reflections
        )

        setExpectations(ValueEntity, "value", "getValue", Integer, DomainPropertyType.VALUE)
        setExpectations(NestedEntity, "child", "getChild", ValueEntity, DomainPropertyType.ENTITY)
        setExpectations(ListOfValuesEntity, "values", "getValues", Integer, DomainPropertyType.LIST_OF_VALUES)
        setExpectations(NestedEntities, "children", "getChildren", ValueEntity, DomainPropertyType.LIST_OF_ENTITIES)
    }

    def "should parse into domain value" (){
        given:
        ValueEntity valueEntityMock = Mock (ValueEntity)
        DBObject valueDbo = new BasicDBObject().append("value", 1)

        when:
        ValueEntity result = testObj.fromDbo(ValueEntity, valueDbo)

        then:
        //noinspection GroovyAssignabilityCheck
        1 * entityProxyFactoryMock.from(ValueEntity, valueDbo, [getValue: 1], methodInterceptors) >> valueEntityMock
        result.is(valueEntityMock)
    }

    def "should parse into list of domain values" (){
        given:
        ListOfValuesEntity valueEntityMock = Mock (ListOfValuesEntity)
        DBObject valuesDbo = new BasicDBObject().append("values", ['one','two'])

        when:
        ListOfValuesEntity result = testObj.fromDbo(ListOfValuesEntity, valuesDbo)

        then:
        //noinspection GroovyAssignabilityCheck
        1 * entityProxyFactoryMock.from(ListOfValuesEntity, valuesDbo, [getValues: ['one', 'two']], methodInterceptors) >> valueEntityMock
        result.is(valueEntityMock)
    }

    def "should parse into entity value" (){
        given:
        NestedEntity nestedEntityMock = Mock (NestedEntity)
        ValueEntity childEntityMock = Mock (ValueEntity)

        DBObject childDbo = new BasicDBObject().append("value", 2)
        DBObject nestedDbo = new BasicDBObject().append("child", childDbo)

        when:
        NestedEntity result = testObj.fromDbo(NestedEntity, nestedDbo)

        then:
        //noinspection GroovyAssignabilityCheck
        1 * entityProxyFactoryMock.from(ValueEntity, childDbo, [getValue: 2], methodInterceptors) >> childEntityMock
        //noinspection GroovyAssignabilityCheck
        1 * entityProxyFactoryMock.from(NestedEntity, nestedDbo, [getChild: childEntityMock], methodInterceptors) >> nestedEntityMock
        result.is(nestedEntityMock)
    }

    def "should parse into list of entities" (){
        given:
        NestedEntities nestedEntitiesMock = Mock (NestedEntities)
        ValueEntity childEntity1Mock = Mock (ValueEntity)
        ValueEntity childEntity2Mock = Mock (ValueEntity)

        DBObject child1Dbo = new BasicDBObject().append("value", 1)
        DBObject child2Dbo = new BasicDBObject().append("value", 2)
        DBObject nestedEntitiesDbo = new BasicDBObject().append("children", [child1Dbo, child2Dbo])

        when:
        NestedEntities result = testObj.fromDbo(NestedEntities, nestedEntitiesDbo)

        then:
        //noinspection GroovyAssignabilityCheck
        1 * entityProxyFactoryMock.from(ValueEntity, child1Dbo, [getValue: 1], methodInterceptors) >> childEntity1Mock
        //noinspection GroovyAssignabilityCheck
        1 * entityProxyFactoryMock.from(ValueEntity, child2Dbo, [getValue: 2], methodInterceptors) >> childEntity2Mock
        //noinspection GroovyAssignabilityCheck
        1 * entityProxyFactoryMock.from(NestedEntities, nestedEntitiesDbo, [getChildren: [childEntity1Mock, childEntity2Mock]], methodInterceptors) >> nestedEntitiesMock
        result.is(nestedEntitiesMock)
    }

    def "should parse into one to one value" (){
        given:
        OneToOneEntity oneToOneEntityMock = Mock (OneToOneEntity)
        OneToOne oneToOneMock = Mock (OneToOne)

        DBObject relationshipDbo = new BasicDBObject().
                append("refName", "ValueEntity").
                append("refId", objectId1Mock)
        DBObject oneToOneDbo = new BasicDBObject().
                append("relationship", relationshipDbo
        )

        DomainPropertyManager domainPropertyManagerMock = setExpectations(OneToOneEntity, "relationship", "getRelationship", ValueEntity, DomainPropertyType.ONE_TO_ONE_TYPE)
        domainPropertyManagerMock.enrich(DboPropertyType.ONE_TO_ONE, relationshipDbo) >> oneToOneMock

        when:
        OneToOneEntity result = testObj.fromDbo(OneToOneEntity, oneToOneDbo)

        then:
        //noinspection GroovyAssignabilityCheck
        1 * entityProxyFactoryMock.from(OneToOneEntity, oneToOneDbo, [getRelationship: oneToOneMock], methodInterceptors) >> oneToOneEntityMock
        result.is(oneToOneEntityMock)
    }

    def "should parse into one to many value" (){
        given:
        OneToManyEntity oneToManyEntityMock = Mock (OneToManyEntity)
        OneToMany oneToManyMock = Mock (OneToMany)
        OneToOne relationship1Mock = Mock (OneToOne)
        OneToOne relationship2Mock = Mock (OneToOne)

        DBObject relationship1Dbo = new BasicDBObject().
                append("refName", "ValueEntity").
                append("refId", objectId1Mock)
        DBObject relationship2Dbo = new BasicDBObject().
                append("refName", "ValueEntity").
                append("refId", objectId2Mock)
        DBObject oneToManyDbo = new BasicDBObject().
                append("relationships", [relationship1Dbo, relationship2Dbo])

        DomainPropertyManager domainPropertyManagerMock = setExpectations(OneToManyEntity, "relationships", "getRelationships", ValueEntity, DomainPropertyType.ONE_TO_MANY_TYPE)
        domainPropertyManagerMock.enrich(DboPropertyType.ONE_TO_ONE, _) >> {type, value ->
            if (value.is(relationship1Dbo)) return relationship1Mock
            if (value.is(relationship2Dbo)) return relationship2Mock
            throw new IllegalStateException()
        }
        domainPropertyManagerMock.enrich(DboPropertyType.ONE_TO_MANY, [relationship1Mock, relationship2Mock]) >> oneToManyMock

        when:
        OneToManyEntity result = testObj.fromDbo(OneToManyEntity, oneToManyDbo)

        then:
        //noinspection GroovyAssignabilityCheck
        1 * entityProxyFactoryMock.from(OneToManyEntity, oneToManyDbo, [getRelationships: oneToManyMock], methodInterceptors) >> oneToManyEntityMock
        result.is(oneToManyEntityMock)
    }

    private DomainPropertyManager setExpectations(Class from, String propertyName, String getterName, Class javaType, DomainPropertyType propertyType) {
        DomainPropertyManager domainPropertyManagerMock = Mock (DomainPropertyManager)
        DomainPropertyDescriptor domainPropertyDescriptorMock = Mock (DomainPropertyDescriptor)

        domainPropertyDescriptorRetrieverMock.findDomainPropertyDescriptor(from, propertyName) >> domainPropertyDescriptorMock
        domainPropertyDescriptorMock.domainPropertyManager >> domainPropertyManagerMock
        domainPropertyDescriptorMock.domainPropertyType >> propertyType
        domainPropertyDescriptorMock.javaType >> javaType
        domainPropertyDescriptorMock.polymorphicTypes >> []

        beanNamingExpertMock.getGetterName(propertyName) >> getterName
        domainPropertyManagerMock
    }

    private static interface OneToManyEntity extends Entity{
        @SuppressWarnings("GroovyUnusedDeclaration")
        public OneToMany<ValueEntity> getRelationships ()
    }

    private static interface OneToOneEntity extends Entity{
        @SuppressWarnings("GroovyUnusedDeclaration")
        public OneToOne<ValueEntity> getRelationship ()
    }

    private static interface NestedEntities extends Entity{
        @SuppressWarnings("GroovyUnusedDeclaration")
        public List<ValueEntity> getChildren ()
    }

    private static interface NestedEntity extends Entity{
        @SuppressWarnings("GroovyUnusedDeclaration")
        public ValueEntity getChild ()
    }

    private static interface ValueEntity extends Entity{
        @SuppressWarnings("GroovyUnusedDeclaration")
        public Integer getValue ()
    }

    private static interface ListOfValuesEntity extends Entity{
        @SuppressWarnings("GroovyUnusedDeclaration")
        public List<String> getValues ()
    }
}
