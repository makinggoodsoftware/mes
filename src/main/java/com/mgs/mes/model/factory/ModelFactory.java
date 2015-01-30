package com.mgs.mes.model.factory;

import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.model.data.ModelData;
import com.mgs.mes.model.data.ModelDataFactory;
import com.mongodb.DBObject;

public class ModelFactory {
	private final DynamicModelFactory dynamicModelFactory;
	private final ModelDataFactory modelDataFactory;

	public ModelFactory(DynamicModelFactory dynamicModelFactory, ModelDataFactory modelDataFactory) {
		this.dynamicModelFactory = dynamicModelFactory;
		this.modelDataFactory = modelDataFactory;
	}

	public <T extends MongoEntity>T from (Class<T> type, DBObject dbObject){
		ModelData modelData = modelDataFactory.fromDbo(type, dbObject);
		return dynamicModelFactory.dynamicModel(type, modelData);
	}

}
