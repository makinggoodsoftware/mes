package com.mgs.config.mes.db;

import com.mgs.mes.db.MongoDao;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;

public class DatabaseConfig {
	public MongoDao dao (String host, int port, String dbName){
		try {
			MongoClient localhost = new MongoClient(host, port);
			DB db = localhost.getDB(dbName);
			return new MongoDao(db);
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
	}
}
