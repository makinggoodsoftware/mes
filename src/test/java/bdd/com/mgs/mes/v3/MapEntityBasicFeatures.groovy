package com.mgs.mes.v3
import com.mgs.config.reflection.ReflectionConfig
import spock.lang.Specification

class MapEntityBasicFeatures extends Specification {
    MapEntityConfig mapEntityConfig = new MapEntityConfig(new ReflectionConfig())
    MapEntityContext context

    def "setup" () {
        context = mapEntityConfig.contextFactory().defaultContext()
    }

    def "should parse value and entity property" (){
        given:
        ValueEntity expectedResult = Mock(ValueEntity)
        expectedResult.asMap() >> [getString:"value"]

        when: "Testing value property"
        ValueEntity valueEntity = context.transform([string:"value"], ValueEntity)

        then:
        valueEntity.getString() == "value"
        valueEntity.hashCode() == [getString:"value"].hashCode()
        valueEntity == expectedResult

        when: "Testing entity property reusing the value property"
        ComplexEntity complexEntity = context.transform([child: [string:"value"]], ComplexEntity)

        then:
        complexEntity.getChild() == valueEntity
        complexEntity.hashCode() == [getChild:valueEntity].hashCode()

        when: "Testing list of entities property reusing the value property"
        ValueEntity valueEntity2 = context.transform([string:"value2"], ValueEntity)
        ComplexEntityList complexEntityList = context.transform([children: [[string:"value"], [string:"value2"]]], ComplexEntityList)

        then:
        complexEntityList.getChildren() == [valueEntity, valueEntity2]
        complexEntityList.hashCode() == [getChildren:[valueEntity, valueEntity2]].hashCode()
    }

    def "should parse list of values property" (){
        given:
        ListOfValuesEntity expectedResult = Mock(ListOfValuesEntity)
        expectedResult.asMap() >> [getStrings:["value1", "value2"]]

        when:
        ListOfValuesEntity result = context.transform([strings:["value1", "value2"]], ListOfValuesEntity)

        then:
        result.getStrings() == ["value1","value2"]
        result.hashCode() == [getStrings:["value1", "value2"]].hashCode()
        result == expectedResult
    }

    private static interface ValueEntity extends MapEntity{
        String getString()
    }

    private static interface ListOfValuesEntity extends MapEntity{
        List<String> getStrings()
    }

    private static interface ComplexEntity extends MapEntity{
        ValueEntity getChild ()
    }

    private static interface ComplexEntityList extends MapEntity{
        List<ValueEntity> getChildren ()
    }

}
