package com.mgs.mes.v2.entity;

import com.mgs.mes.model.Entity;
import com.mgs.mes.v2.polymorphism.PolymorphismManager;
import com.mgs.mes.v2.property.descriptor.DomainPropertyDescriptor;
import com.mgs.mes.v2.property.descriptor.DomainPropertyDescriptorRetriever;
import com.mgs.mes.v2.property.type.dbo.ComplexityType;
import com.mgs.mes.v2.property.type.dbo.DboPropertyType;
import com.mgs.mes.v2.property.type.domain.DomainPropertyType;
import com.mgs.reflection.BeanNamingExpert;
import com.mongodb.DBObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class EntityFactory {
	private final EntityProxyFactory entityProxyFactory;
	private final DomainPropertyDescriptorRetriever domainPropertyDescriptorRetriever;
	private final PolymorphismManager polymorphismManager;
	private final BeanNamingExpert beanNamingExpert;

	public EntityFactory(EntityProxyFactory entityProxyFactory, DomainPropertyDescriptorRetriever domainPropertyDescriptorRetriever, PolymorphismManager polymorphismManager, BeanNamingExpert beanNamingExpert) {
		this.entityProxyFactory = entityProxyFactory;
		this.domainPropertyDescriptorRetriever = domainPropertyDescriptorRetriever;
		this.polymorphismManager = polymorphismManager;
		this.beanNamingExpert = beanNamingExpert;
	}

	public <T extends Entity> T fromDbo (Class<T> type, DBObject dbObject){
		return entityProxyFactory.from(
				type,
				dbObject,
				domainValues (type, dbObject)
		);
	}

	public <T extends Entity> Map<String, Object> domainValues (Class<T> parentType, DBObject dbObject){
		Map<String, Object> domainValues = new HashMap<>();
		//noinspection unchecked
		Map<String, Object> map = dbObject.toMap();
		map.entrySet().stream().forEach((it) -> {
			Object domainValue = domainValue(parentType, it.getKey(), it.getValue());
			String getterName = beanNamingExpert.getGetterName(it.getKey());
			domainValues.put(getterName, domainValue);
		});
		return domainValues;
	}

	private <T extends Entity> Object domainValue(Class<T> parentType, String key, Object value) {
		DomainPropertyDescriptor domainPropertyDescriptor = domainPropertyDescriptorRetriever.findDomainPropertyDescriptor(parentType, key);
		DomainPropertyType domainPropertyType = domainPropertyDescriptor.getDomainPropertyType();

		switch (domainPropertyType.getMultiplicityType()){
			case COMPOSITE:
				//noinspection unchecked
				List<Object> values = (List<Object>) value;
				List<Object> childValues = values.stream().map((it) -> calculateDboValue(domainPropertyDescriptor, domainPropertyType.getValueDescriptor(), parentType, key, it)).collect(toList());
				return calculateDboValue(domainPropertyDescriptor, domainPropertyType.getGroupDescriptor().get(), parentType, key, childValues);
			case  SINGLE:
				return calculateDboValue(domainPropertyDescriptor, domainPropertyType.getValueDescriptor(), parentType, key, value);
		}

		throw new IllegalStateException();
	}

	private <T extends Entity> Object calculateDboValue(DomainPropertyDescriptor domainDataManager, DboPropertyType dboPropertyType, Class<T> parentType, String key, Object value) {
		switch (dboPropertyType.getRequiresManipulation()){
			case NO_MANIPULATION:
				return calculateSimpleDboValue(domainDataManager, dboPropertyType.getComplexityType().get(), parentType, key, value);
			case REQUIRES_MANIPULATION:
				return domainDataManager.getDomainPropertyManager().enrich(dboPropertyType, value);
		}
		throw new IllegalStateException();
	}

	private <T extends Entity> Object calculateSimpleDboValue(DomainPropertyDescriptor domainDataManager, ComplexityType complexityType, Class<T> parentType, String key, Object value) {
		switch (complexityType){
			case VALUE:
				return value;
			case SIMPLE_ENTITY:
				DBObject asDbo = (DBObject) value;
				Class childType;
				List<Class> polymorphicTypes = domainDataManager.getPolymorphicTypes();
				if (polymorphicTypes.size() > 1) {
					childType = polymorphismManager.resolve (polymorphicTypes, value);
				} else {
					childType = domainDataManager.getJavaType();
				}
				return fromDbo(childType, asDbo);
		}
		throw new IllegalStateException();
	}
}
