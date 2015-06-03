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
		EntityWithBuilderMethods result = context.newEntity (EntityWithBuilderMethods, {into ->
			into.withString("value")
		} as EntityMapBuilder<EntityWithBuilderMethods>)

		then:
		result.string == "value"

		when:
        EntityWithBuilderMethods result2 = result.withString("value2")

        then:
        ! result2.is(result)
        result2.string == "value2"
        result.string == "value"
	}

	private static interface EntityWithBuilderMethods extends MapEntity {
		String getString ()

		EntityWithBuilderMethods withString (String string)
	}
}
