package com.mgs.mes.v2.config;

import com.mgs.config.reflection.ReflectionConfig;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.OneToMany;
import com.mgs.mes.model.OneToOne;
import com.mgs.mes.v2.polymorphism.PolymorphismManager;
import com.mgs.mes.v2.property.descriptor.DomainPropertyDescriptorRetriever;
import com.mgs.mes.v2.property.manager.DomainPropertyManager;
import com.mgs.mes.v2.property.manager.impl.BasePropertyManager;
import com.mgs.mes.v2.property.type.domain.DomainPropertyType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DescriptorConfig {
	private final ReflectionConfig reflectionConfig;

	public DescriptorConfig(ReflectionConfig reflectionConfig) {
		this.reflectionConfig = reflectionConfig;
	}

	public DomainPropertyDescriptorRetriever domainPropertyDescriptorRetriever() {
		Map<DomainPropertyType, DomainPropertyManager> configuration = new HashMap<>();
		configuration.put(
				DomainPropertyType.VALUE,
				new BasePropertyManager(reflectionConfig.beanNamingExpert(), reflectionConfig.fieldAccessorParser(),
						fieldAccessor -> reflectionConfig.reflections().isSimple(fieldAccessor.getDeclaredType())
				)
		);
		configuration.put(
				DomainPropertyType.LIST_OF_VALUES,
				new BasePropertyManager(reflectionConfig.beanNamingExpert(), reflectionConfig.fieldAccessorParser(),
						fieldAccessor ->
								reflectionConfig.reflections().isAssignableTo(fieldAccessor.getDeclaredType(), Collection.class) &&
								fieldAccessor.getParametrizedTypes().size() == 1 &&
								reflectionConfig.reflections().isSimple(fieldAccessor.getParametrizedTypes().get(0).getSpecificClass().get())
				)
		);
		configuration.put(
				DomainPropertyType.ENTITY,
				new BasePropertyManager(reflectionConfig.beanNamingExpert(), reflectionConfig.fieldAccessorParser(),
						fieldAccessor ->
								reflectionConfig.reflections().isAssignableTo(fieldAccessor.getDeclaredType(), Entity.class) &&
								! reflectionConfig.reflections().isAssignableTo(fieldAccessor.getDeclaredType(), OneToOne.class) &&
								! reflectionConfig.reflections().isAssignableTo(fieldAccessor.getDeclaredType(), OneToMany.class)
				)
		);
		configuration.put(
				DomainPropertyType.LIST_OF_ENTITIES,
				new BasePropertyManager(reflectionConfig.beanNamingExpert(), reflectionConfig.fieldAccessorParser(),
						fieldAccessor ->
								reflectionConfig.reflections().isAssignableTo(fieldAccessor.getDeclaredType(), Collection.class) &&
								fieldAccessor.getParametrizedTypes().size() == 1 &&
								reflectionConfig.reflections().isAssignableTo(fieldAccessor.getParametrizedTypes().get(0).getSpecificClass().get(), Entity.class)
				)
		);
		configuration.put(
				DomainPropertyType.ONE_TO_ONE_TYPE,
				new BasePropertyManager(reflectionConfig.beanNamingExpert(), reflectionConfig.fieldAccessorParser(),
						fieldAccessor ->
								reflectionConfig.reflections().isAssignableTo(fieldAccessor.getDeclaredType(), OneToOne.class)
				)
		);
		configuration.put(
				DomainPropertyType.ONE_TO_MANY_TYPE,
				new BasePropertyManager(reflectionConfig.beanNamingExpert(), reflectionConfig.fieldAccessorParser(),
						fieldAccessor ->
								reflectionConfig.reflections().isAssignableTo(fieldAccessor.getDeclaredType(), OneToMany.class)
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
