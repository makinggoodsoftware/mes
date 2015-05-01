package com.mgs.mes.v2.config;

import com.mgs.config.reflection.ReflectionConfig;
import com.mgs.mes.v2.entity.property.descriptor.DomainPropertyDescriptorRetriever;
import com.mgs.mes.v2.entity.property.manager.BasePropertyManager;
import com.mgs.mes.v2.entity.property.manager.DomainPropertyManager;
import com.mgs.mes.v2.entity.property.type.domain.DomainPropertyType;
import com.mgs.mes.v2.polymorphism.PolymorphismManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DescriptorConfig {
	private final ManagerConfig managerConfig;
	private final ReflectionConfig reflectionConfig;

	public DescriptorConfig(ManagerConfig managerConfig, ReflectionConfig reflectionConfig) {
		this.managerConfig = managerConfig;
		this.reflectionConfig = reflectionConfig;
	}

	public DomainPropertyDescriptorRetriever domainPropertyDescriptorRetriever() {
		Map<DomainPropertyType, DomainPropertyManager> configuration = new HashMap<>();
		configuration.put(
				DomainPropertyType.VALUE,
				new BasePropertyManager(
						reflectionConfig.beanNamingExpert(),
						reflectionConfig.fieldAccessorParser(),
						managerConfig.entityManager()::isSimpleValue,
						Optional.empty()
				)
		);
		configuration.put(
				DomainPropertyType.LIST_OF_VALUES,
				new BasePropertyManager(
						reflectionConfig.beanNamingExpert(),
						reflectionConfig.fieldAccessorParser(),
						managerConfig.entityManager()::isListOfValues,
						Optional.empty()
				)
		);
		configuration.put(
				DomainPropertyType.ENTITY,
				new BasePropertyManager(
						reflectionConfig.beanNamingExpert(),
						reflectionConfig.fieldAccessorParser(),
						managerConfig.entityManager()::isSimpleEntity,
						Optional.empty()
				)
		);
		configuration.put(
				DomainPropertyType.LIST_OF_ENTITIES,
				new BasePropertyManager(
						reflectionConfig.beanNamingExpert(),
						reflectionConfig.fieldAccessorParser(),
						managerConfig.entityManager()::isListOfEntities,
						Optional.empty()
				)
		);
		return new DomainPropertyDescriptorRetriever(
				configuration,
				polymorphismManager()
		);
	}



	private PolymorphismManager polymorphismManager() {
		return new PolymorphismManager(
				reflectionConfig.fieldAccessorParser(),
				reflectionConfig.beanNamingExpert(),
				reflectionConfig.reflections());
	}
}
