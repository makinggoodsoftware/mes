package com.mgs.mes.meta.init;

import com.mgs.mes.db.MongoDao;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;

public class MongoDaoFactory {
	public MongoDao create(String host, int port, String dbName){
		try {
			MongoClient localhost = new MongoClient(host, port);
			DB db = localhost.getDB(dbName);
		return new MongoDao(db);
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
	}
}
