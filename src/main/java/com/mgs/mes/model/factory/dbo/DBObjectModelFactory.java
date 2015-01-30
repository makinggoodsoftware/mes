package com.mgs.mes.model.factory.dbo;

import com.mgs.mes.model.MongoEntity;
import com.mgs.mes.model.data.ModelData;
import com.mgs.mes.model.data.ModelDataFactory;
import com.mgs.mes.model.factory.ModelFactory;
import com.mongodb.DBObject;

public class DBObjectModelFactory implements ModelFactory<DBObject> {
	private final ModelFactory<ModelData> modelDataModelFactory;
	private final ModelDataFactory modelDataFactory;

	public DBObjectModelFactory(ModelFactory<ModelData> modelDataModelFactory, ModelDataFactory modelDataFactory) {
		this.modelDataModelFactory = modelDataModelFactory;
		this.modelDataFactory = modelDataFactory;
	}

	@Override
	public <T extends MongoEntity>T from(Class<T> type, DBObject dbObject){
		ModelData modelData = modelDataFactory.fromDbo(type, dbObject);
		return modelDataModelFactory.from(type, modelData);
	}

}
