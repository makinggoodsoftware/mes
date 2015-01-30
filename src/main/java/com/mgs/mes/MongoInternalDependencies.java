package com.mgs.mes;

import com.mgs.mes.model.ModelValidator;
import com.mgs.mes.model.data.ModelDataBuilderFactory;
import com.mgs.mes.model.data.ModelDataFactory;
import com.mgs.mes.model.data.transformer.DboTransformer;
import com.mgs.mes.model.data.transformer.FieldAccessorMapTransformer;
import com.mgs.mes.model.factory.DynamicModelFactory;
import com.mgs.mes.model.factory.ModelFactory;
import com.mgs.mes.utils.MongoEntities;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mgs.reflection.Reflections;

public class MongoInternalDependencies {
	private final ModelFactory modelFactory;
	private final ModelValidator modelValidator;
	private final ModelDataBuilderFactory modelDataBuilderFactory;
	private final FieldAccessorParser fieldAccessorParser;
	private final DynamicModelFactory dynamicModelFactory;
	private final BeanNamingExpert beanNamingExpert;
	private final MongoEntities mongoEntities;

	public static MongoInternalDependencies init () {
		DynamicModelFactory dynamicModelFactory = new DynamicModelFactory();
		BeanNamingExpert beanNamingExpert = new BeanNamingExpert();
		FieldAccessorParser fieldAccessorParser = new FieldAccessorParser(beanNamingExpert);

		Reflections reflections = new Reflections();
		DboTransformer dboModelDataTransformer = new DboTransformer(dynamicModelFactory, beanNamingExpert, fieldAccessorParser);
		FieldAccessorMapTransformer mapModelDataTransformer = new FieldAccessorMapTransformer();
		ModelDataFactory modelDataFactory = new ModelDataFactory(dboModelDataTransformer, mapModelDataTransformer);

		ModelValidator modelValidator = new ModelValidator(reflections, fieldAccessorParser);
		ModelDataBuilderFactory modelDataBuilderFactory = new ModelDataBuilderFactory(modelDataFactory, beanNamingExpert, fieldAccessorParser);
		ModelFactory modelFactory = new ModelFactory(dynamicModelFactory, modelDataFactory);
		MongoEntities mongoEntities = new MongoEntities();

		return new MongoInternalDependencies(modelFactory, modelValidator, modelDataBuilderFactory, fieldAccessorParser, dynamicModelFactory, beanNamingExpert, mongoEntities);
	}

	private MongoInternalDependencies(ModelFactory modelFactory, ModelValidator modelValidator, ModelDataBuilderFactory modelDataBuilderFactory, FieldAccessorParser fieldAccessorParser, DynamicModelFactory dynamicModelFactory, BeanNamingExpert beanNamingExpert, MongoEntities mongoEntities) {
		this.modelFactory = modelFactory;
		this.modelValidator = modelValidator;
		this.modelDataBuilderFactory = modelDataBuilderFactory;
		this.fieldAccessorParser = fieldAccessorParser;
		this.dynamicModelFactory = dynamicModelFactory;
		this.beanNamingExpert = beanNamingExpert;
		this.mongoEntities = mongoEntities;
	}

	public ModelFactory getModelFactory() {
		return modelFactory;
	}

	public ModelValidator getModelValidator() {
		return modelValidator;
	}

	public ModelDataBuilderFactory getModelDataBuilderFactory() {
		return modelDataBuilderFactory;
	}

	public FieldAccessorParser getFieldAccessorParser() {
		return fieldAccessorParser;
	}

	public DynamicModelFactory getDynamicModelFactory() {
		return dynamicModelFactory;
	}

	public BeanNamingExpert getBeanNamingExpert() {
		return beanNamingExpert;
	}

	public MongoEntities getMongoEntities() {
		return mongoEntities;
	}
}
