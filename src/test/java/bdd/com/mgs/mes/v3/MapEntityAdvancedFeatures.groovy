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

    private static interface NonOptionalEntity extends MapEntity{
        String getString ()
    }

    private static interface OptionalEntity extends MapEntity{
        Optional<String> getString ()
    }

}
