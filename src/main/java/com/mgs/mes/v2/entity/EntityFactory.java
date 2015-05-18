package com.mgs.mes.v2.entity;

import com.mgs.mes.model.Entity;
import com.mgs.mes.v2.entity.property.descriptor.DomainPropertyDescriptor;
import com.mgs.mes.v2.entity.property.descriptor.DomainPropertyDescriptorRetriever;
import com.mgs.mes.v2.entity.property.type.dbo.DboPropertyType;
import com.mgs.mes.v2.entity.property.type.domain.DomainPropertyType;
import com.mgs.mes.v2.polymorphism.PolymorphismManager;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.GenericType;
import com.mgs.reflection.Reflections;
import com.mongodb.DBObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * This class loops through all the properties of a DBO against a class that represents
 * the DBO and which must implement the interface Entity
 * For each property, finds the associated getter in the class and using the information
 * of the getter marshalls the data.
 */
public class EntityFactory {
	private final EntityProxyFactory entityProxyFactory;
	private final DomainPropertyDescriptorRetriever domainPropertyDescriptorRetriever;
	private final PolymorphismManager polymorphismManager;
	private final BeanNamingExpert beanNamingExpert;
	private final Reflections reflections;

	public EntityFactory(EntityProxyFactory entityProxyFactory, DomainPropertyDescriptorRetriever domainPropertyDescriptorRetriever, PolymorphismManager polymorphismManager, BeanNamingExpert beanNamingExpert, Reflections reflections) {
		this.entityProxyFactory = entityProxyFactory;
		this.domainPropertyDescriptorRetriever = domainPropertyDescriptorRetriever;
		this.polymorphismManager = polymorphismManager;
		this.beanNamingExpert = beanNamingExpert;
		this.reflections = reflections;
	}

	public <T extends Entity> T fromDbo (Class<T> type, DBObject dbObject
	){
		List<GenericType> genericTypes = reflections.extractGenericClasses(type);
		if (genericTypes.size() > 1) throw new IllegalStateException();

		//noinspection unchecked
		Optional<Class<? extends Entity>> wrappedType = (genericTypes.size() == 0) ?
				Optional.empty() :
				null;


		return entityProxyFactory.from(
				type,
				wrappedType,
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
				return calculateDboValue(domainPropertyDescriptor, domainPropertyType.getValueDescriptor(), parentType, key, childValues);
			case  SINGLE:
				return calculateDboValue(domainPropertyDescriptor, domainPropertyType.getValueDescriptor(), parentType, key, value);
		}

		throw new IllegalStateException();
	}

	private <T extends Entity> Object calculateDboValue(DomainPropertyDescriptor domainDataManager, DboPropertyType dboPropertyType, Class<T> parentType, String key, Object value) {
		switch (dboPropertyType.getComplexityType().get()){
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
