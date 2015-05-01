package com.mgs.mes.v2.entity.property.descriptor

import com.mgs.config.reflection.ReflectionConfig
import com.mgs.mes.model.Entity
import com.mgs.mes.model.OneToMany
import com.mgs.mes.model.OneToOne
import com.mgs.mes.v2.config.DescriptorConfig
import spock.lang.Specification

import static com.mgs.mes.v2.entity.property.type.domain.DomainPropertyType.*

class DomainPropertyDescriptorRetrieverSpecification extends Specification {
    DescriptorConfig descriptorConfig
    DomainPropertyDescriptorRetriever testObj

    def "setup" (){
        descriptorConfig = new DescriptorConfig(managerConfig, new ReflectionConfig())
        testObj = descriptorConfig.domainPropertyDescriptorRetriever()
    }

    def "should retrieve value descriptor" (){
        when:
        DomainPropertyDescriptor result = testObj.findDomainPropertyDescriptor(SampleEntity, "string")

        then:
        result.polymorphicTypes == [String]
        result.javaType == String
        result.domainPropertyType == VALUE
    }

    def "should retrieve list of values descriptor" (){
        when:
        DomainPropertyDescriptor result = testObj.findDomainPropertyDescriptor(SampleEntity, "integers")

        then:
        result.polymorphicTypes == [Integer]
        result.javaType == Integer
        result.domainPropertyType == LIST_OF_VALUES
    }

    def "should retrieve entity descriptor" (){
        when:
        DomainPropertyDescriptor result = testObj.findDomainPropertyDescriptor(SampleEntity, "child")

        then:
        result.polymorphicTypes == [SampleChildEntity]
        result.javaType == SampleChildEntity
        result.domainPropertyType == ENTITY
    }

    def "should retrieve list of entities descriptor" (){
        when:
        DomainPropertyDescriptor result = testObj.findDomainPropertyDescriptor(SampleEntity, "children")

        then:
        result.polymorphicTypes == [SampleChildEntity]
        result.javaType == SampleChildEntity
        result.domainPropertyType == LIST_OF_ENTITIES
    }

    def "should retrieve one to one entity descriptor" (){
        when:
        DomainPropertyDescriptor result = testObj.findDomainPropertyDescriptor(SampleEntity, "relationship")

        then:
        result.polymorphicTypes == [SampleChildEntity]
        result.javaType == SampleChildEntity
        result.domainPropertyType == ONE_TO_ONE_TYPE
    }

    def "should retrieve one to many entity descriptor" (){
        when:
        DomainPropertyDescriptor result = testObj.findDomainPropertyDescriptor(SampleEntity, "relationships")

        then:
        result.polymorphicTypes == [SampleChildEntity]
        result.javaType == SampleChildEntity
        result.domainPropertyType == ONE_TO_MANY_TYPE
    }

    private static interface SampleEntity extends Entity{
        String getString ()
        List<Integer> getIntegers ()
        SampleChildEntity getChild()
        List<SampleChildEntity> getChildren()
        OneToOne<SampleChildEntity> getRelationship()
        OneToMany<SampleChildEntity> getRelationships()
    }

    private static interface SampleChildEntity extends Entity{
        String getString ()
        List<Integer> getIntegers ()
    }
}
