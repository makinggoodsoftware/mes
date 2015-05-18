package com.mgs.mes.v3.mapping

import com.mgs.config.reflection.ReflectionConfig
import com.mgs.mes.v3.mapper.MapEntity
import com.mgs.mes.v3.mapper.MapEntityConfig
import com.mgs.mes.v3.mapper.MapEntityContext
import com.mgs.mes.v3.mapper.Mapping
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

    def "should parse simple parametrised entities" (){
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

    def "should parse collection of parametrised entities" (){
        when:
        StringCollectionParametrizedEntityTemplate stringCollection = context.transform([them:["it", "silly"]], StringCollectionParametrizedEntityTemplate)

        then:
        stringCollection.them == ["it", "silly"]

        when:
        EntityCollectionParametrizedEntityTemplate entityCollection = context.transform([them:[[value:"it"], [value:"silly"]]], EntityCollectionParametrizedEntityTemplate)

        then:
        entityCollection.them.size() == 2
        entityCollection.them.get(0).value == "it"
        entityCollection.them.get(1).value == "silly"
    }

    def "should parse complex embedded parametrised entities" (){
        when:
        ComplexEmbeddedParametrizedEntityTemplate complex = context.transform([complex:[it: ["value": "silly"]]], ComplexEmbeddedParametrizedEntityTemplate)

        then:
        complex.complex.it.value == "silly"
    }

    private static interface CollectionParametrizedEntityTemplate<T> extends MapEntity{
        List<T> getThem ()
    }

    private static interface CollectionComplexParametrizedEntityTemplate<T> extends MapEntity{
    }

    private static interface StringCollectionParametrizedEntityTemplate extends CollectionParametrizedEntityTemplate<String>{
    }

    private static interface EntityCollectionParametrizedEntityTemplate extends CollectionParametrizedEntityTemplate<SimpleEntity>{
    }

    private static interface ParametrizedEntityTemplate<T> extends MapEntity{
        T getIt ()
    }

    private static interface StringParametrizedEntityTemplate extends ParametrizedEntityTemplate<String>{
    }

    private static interface EmbeddedParametrizedEntityTemplate extends MapEntity{
        ParametrizedEntityTemplate<String> getEmbedded();
    }

    private static interface ComplexEmbeddedParametrizedEntityTemplate extends MapEntity{
        ParametrizedEntityTemplate<SimpleEntity> getComplex();
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

    private static interface SimpleEntity extends MapEntity{
        String getValue();
    }
}
