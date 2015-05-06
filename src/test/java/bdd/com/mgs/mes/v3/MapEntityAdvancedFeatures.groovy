package com.mgs.mes.v3

import com.mgs.config.reflection.ReflectionConfig
import spock.lang.Specification

class MapEntityAdvancedFeatures extends Specification {
    MapEntityConfig mapEntityConfig = new MapEntityConfig(new ReflectionConfig())
    MapEntityContext context

    def "setup" () {
        context = mapEntityConfig.contextFactory().defaultContext()
    }

    def "should fail if there is an unmapped non optional field in the interface" (){
        when:
        context.transform([:], NonOptionalEntity).getString()

        then:
        thrown(Exception)
    }

    def "should parse optional values" (){
        expect:
        context.transform([string:"value"], OptionalEntity).getString() == Optional.of("value")

        and:
        context.transform([:], OptionalEntity).getString() == Optional.empty()
    }

    def "should parse custom field values" (){
        when:
        CustomFieldNameEntity result = context.transform(["_id":"A"], CustomFieldNameEntity)

        then:
        result.getId() == "A"

        when:
        context.transform(["id":"A"], CustomFieldNameEntity).getId()

        then:
        thrown(Exception)
    }

    def "should parse parametrised entities" (){
        when:
        EmbeddedParametrizedEntityTemplate embeddedValue = context.transform([embedded:["it":"silly"]], EmbeddedParametrizedEntityTemplate)

        then:
        embeddedValue.embedded.it == "silly"

        when:
        StringParametrizedEntityTemplate result = context.transform(["it":"silly"], StringParametrizedEntityTemplate)

        then:
        result.getIt() == "silly"

        when:
        context.transform(["it":"silly"], ParametrizedEntityTemplate)

        then:
        thrown(Exception)
    }

    private static interface ParametrizedEntityTemplate<T> extends MapEntity{
        @Parametrizable
        T getIt ()
    }

    private static interface StringParametrizedEntityTemplate extends ParametrizedEntityTemplate<String>{
        @Override
        @Parametrized
        String getIt();
    }

    private static interface EmbeddedParametrizedEntityTemplate extends MapEntity{
        ParametrizedEntityTemplate<String> getEmbedded();
    }

    private static interface NonOptionalEntity extends MapEntity{
        String getString ()
    }

    private static interface OptionalEntity extends MapEntity{
        Optional<String> getString ()
    }

    private static interface CustomFieldNameEntity extends MapEntity{
        @Mapping(mapFieldName = "_id")
        String getId ()
    }

}
