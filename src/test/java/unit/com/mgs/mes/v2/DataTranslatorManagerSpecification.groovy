package com.mgs.mes.v2
import com.mgs.mes.v2.property.descriptor.DomainPropertyDescriptorRetriever
import spock.lang.Specification

class DataTranslatorManagerSpecification extends Specification {
    DomainPropertyDescriptorRetriever testObj

    def "should translate to value domain type" (){
        expect: "WIP"
        false
    }

    public static interface EntityType {
        public String getString ();
    }
}
