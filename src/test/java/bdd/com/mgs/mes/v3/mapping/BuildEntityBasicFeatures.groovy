package com.mgs.mes.v3.mapping
import com.mgs.config.reflection.ReflectionConfig
import com.mgs.mes.v3.mapper.EntityMapBuilder
import com.mgs.mes.v3.mapper.MapEntity
import com.mgs.mes.v3.mapper.MapEntityConfig
import com.mgs.mes.v3.mapper.MapEntityContext
import spock.lang.Specification

public class BuildEntityBasicFeatures extends Specification{
	MapEntityConfig mapEntityConfig = new MapEntityConfig(new ReflectionConfig())
	MapEntityContext context

	def "setup" () {
		context = mapEntityConfig.contextFactory().defaultContext()
	}

	def "should build basic entity" (){
		when:
		SimpleEntityWithBuilderMethods result = context.newEntity (SimpleEntityWithBuilderMethods, {into ->
			into.withString("value")
		} as EntityMapBuilder<SimpleEntityWithBuilderMethods>)

		then:
		result.string == "value"
		result.asDomainMap() == [string:"value"]

		when:
        SimpleEntityWithBuilderMethods result2 = result.withString("value2")

        then:
        ! result2.is(result)
        result2.string == "value2"
        result.string == "value"
	}

	def "should build complex entity" (){
		when:
		ComplexEntityWithBuilderMethods result = context.newEntity (ComplexEntityWithBuilderMethods, {parent ->
			parent.withChild(context.newEntity (SimpleEntityWithBuilderMethods, {child ->
				child.withString("value")
			} as EntityMapBuilder<SimpleEntityWithBuilderMethods>))
		} as EntityMapBuilder<ComplexEntityWithBuilderMethods>)

		then:
		result.child.string == "value"


        when:
        ComplexEntityWithBuilderMethods result2 = result.withChild(context.newEntity(SimpleEntityWithBuilderMethods, { child ->
            child.withString("value2")
        } as EntityMapBuilder<SimpleEntityWithBuilderMethods>))

		then:
		! result2.is(result)
		result2.child.string == "value2"
		result.child.string == "value"
	}


	static interface SimpleEntityWithBuilderMethods extends MapEntity {
		String getString ()

		SimpleEntityWithBuilderMethods withString (String string)
	}

	static interface ComplexEntityWithBuilderMethods extends MapEntity {
		SimpleEntityWithBuilderMethods getChild()

        ComplexEntityWithBuilderMethods withChild (SimpleEntityWithBuilderMethods child)
	}
}
