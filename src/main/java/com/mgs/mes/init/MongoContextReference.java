package com.mgs.mes.init;

public class MongoContextReference {
	private MongoContext mongoContext;

	public void set (MongoContext context){
		this.mongoContext = context;
	}

	public MongoContext get() {
		return mongoContext;
	}
}
