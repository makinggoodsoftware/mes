package com.mgs.mes.init;

import com.mgs.mes.model.Validator;
import com.mgs.mes.model.data.ModelData;
import com.mgs.mes.model.data.ModelDataBuilderFactory;
import com.mgs.mes.model.data.ModelDataFactory;
import com.mgs.mes.model.data.transformer.DboTransformer;
import com.mgs.mes.model.data.transformer.FieldAccessorMapTransformer;
import com.mgs.mes.model.factory.ModelFactory;
import com.mgs.mes.model.factory.dbo.DBObjectModelFactory;
import com.mgs.mes.model.factory.modelData.ModelDataModelFactory;
import com.mgs.mes.utils.MongoEntities;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mgs.reflection.Reflections;
import com.mongodb.DBObject;

public class MongoInternalDependencies {
	private final Validator validator;
	private final ModelDataBuilderFactory modelDataBuilderFactory;
	private final FieldAccessorParser fieldAccessorParser;
	private final ModelFactory<DBObject> DBOModelFactory;
	private final ModelFactory<ModelData> modelDataModelFactory;
	private final BeanNamingExpert beanNamingExpert;
	private final MongoEntities mongoEntities;
	private final MongoContextFactory mongoContextFactory;
	private final UnlinkedMongoContextFactory unlinkedMongoContextFactory;

	public static MongoInternalDependencies init () {
		ModelFactory<ModelData> modelDataModelFactory = new ModelDataModelFactory();
		BeanNamingExpert beanNamingExpert = new BeanNamingExpert();
		FieldAccessorParser fieldAccessorParser = new FieldAccessorParser(beanNamingExpert);

		Reflections reflections = new Reflections();
		DboTransformer dboModelDataTransformer = new DboTransformer(modelDataModelFactory, beanNamingExpert, fieldAccessorParser);
		FieldAccessorMapTransformer mapModelDataTransformer = new FieldAccessorMapTransformer();
		ModelDataFactory modelDataFactory = new ModelDataFactory(dboModelDataTransformer, mapModelDataTransformer);

		Validator validator = new Validator(reflections, fieldAccessorParser);
		ModelDataBuilderFactory modelDataBuilderFactory = new ModelDataBuilderFactory(modelDataFactory, beanNamingExpert, fieldAccessorParser);
		ModelFactory<DBObject> modelFactory = new DBObjectModelFactory(modelDataModelFactory, modelDataFactory);
		MongoEntities mongoEntities = new MongoEntities();

		MongoContextFactory mongoContextFactory1 = new MongoContextFactory();
		UnlinkedMongoContextFactory unlinkedMongoContextFactory1 = new UnlinkedMongoContextFactory();

		return new MongoInternalDependencies(
				modelFactory,
				validator,
				modelDataBuilderFactory,
				fieldAccessorParser,
				modelDataModelFactory,
				beanNamingExpert,
				mongoEntities,
				mongoContextFactory1,
				unlinkedMongoContextFactory1);
	}

	private MongoInternalDependencies(ModelFactory<DBObject> DBOModelFactory, Validator validator, ModelDataBuilderFactory modelDataBuilderFactory, FieldAccessorParser fieldAccessorParser, ModelFactory<ModelData> modelDataModelFactory, BeanNamingExpert beanNamingExpert, MongoEntities mongoEntities, MongoContextFactory mongoContextFactory, UnlinkedMongoContextFactory unlinkedMongoContextFactory) {
		this.DBOModelFactory = DBOModelFactory;
		this.validator = validator;
		this.modelDataBuilderFactory = modelDataBuilderFactory;
		this.fieldAccessorParser = fieldAccessorParser;
		this.modelDataModelFactory = modelDataModelFactory;
		this.beanNamingExpert = beanNamingExpert;
		this.mongoEntities = mongoEntities;
		this.mongoContextFactory = mongoContextFactory;
		this.unlinkedMongoContextFactory = unlinkedMongoContextFactory;
	}

	public ModelFactory<DBObject> getDBOModelFactory() {
		return DBOModelFactory;
	}

	public Validator getValidator() {
		return validator;
	}

	public ModelDataBuilderFactory getModelDataBuilderFactory() {
		return modelDataBuilderFactory;
	}

	public FieldAccessorParser getFieldAccessorParser() {
		return fieldAccessorParser;
	}

	public ModelFactory<ModelData> getModelDataModelFactory() {
		return modelDataModelFactory;
	}

	public BeanNamingExpert getBeanNamingExpert() {
		return beanNamingExpert;
	}

	public MongoEntities getMongoEntities() {
		return mongoEntities;
	}

	public MongoContextFactory getMongoContextFactory() {
		return mongoContextFactory;
	}

	public UnlinkedMongoContextFactory getUnlinkedMongoContextFactory() {
		return unlinkedMongoContextFactory;
	}
}
