package com.mgs.mes.meta.init;

public class MongoContextReference {
	private MongoContext mongoContext;

	public void set (MongoContext context){
		this.mongoContext = context;
	}

	public MongoContext get() {
		return mongoContext;
	}
}
