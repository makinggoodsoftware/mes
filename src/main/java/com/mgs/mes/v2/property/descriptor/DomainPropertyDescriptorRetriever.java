package com.mgs.mes.v2.property.descriptor;

import com.mgs.mes.model.Entity;
import com.mgs.mes.v2.polymorphism.PolymorphismDescriptor;
import com.mgs.mes.v2.polymorphism.PolymorphismManager;
import com.mgs.mes.v2.property.manager.DomainPropertyManager;
import com.mgs.mes.v2.property.type.domain.DomainPropertyType;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class DomainPropertyDescriptorRetriever {
	private final Map<DomainPropertyType, DomainPropertyManager> configuration;
	private final PolymorphismManager polymorphismManager;

	public DomainPropertyDescriptorRetriever(Map<DomainPropertyType, DomainPropertyManager> configuration, PolymorphismManager polymorphismManager) {
		this.configuration = configuration;
		this.polymorphismManager = polymorphismManager;
	}


	public DomainPropertyDescriptor findDomainPropertyDescriptor(Class<? extends Entity> parentType, String key) {
		List<Map.Entry<DomainPropertyType, DomainPropertyManager>> applicableManagers = configuration.entrySet().stream().
				filter((entry) -> entry.getValue().applies(parentType, key)).
				collect(toList());

		if (applicableManagers.size() != 1) throw new IllegalStateException();

		PolymorphismDescriptor polymorphismDescriptor = polymorphismManager.analise(parentType, key);
		return new DomainPropertyDescriptor(
				applicableManagers.get(0).getKey(),
				applicableManagers.get(0).getValue(),
				polymorphismDescriptor.getType(),
				polymorphismDescriptor.getPolymorphicTypes());
	}
}
