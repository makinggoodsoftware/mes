package com.mgs.config;

import com.mgs.config.mes.build.BuildConfig;
import com.mgs.config.mes.context.ContextConfig;
import com.mgs.config.mes.db.DatabaseConfig;
import com.mgs.config.mes.meta.MetaConfig;
import com.mgs.config.reflection.ReflectionConfig;
import com.mgs.mes.db.MongoDao;

public class MesConfigFactory {
	private final MetaConfig metaConfig = new MetaConfig(new ReflectionConfig());

	public MesConfig simple (String hostName, int port, String dbName){
		DatabaseConfig databaseConfig = new DatabaseConfig();
		MongoDao dao = databaseConfig.dao(hostName, port, dbName);
		ReflectionConfig reflectionConfig = new ReflectionConfig();
		return new MesConfig(
				new ContextConfig(
						new MetaConfig(reflectionConfig),
						new BuildConfig(reflectionConfig, metaConfig),
						reflectionConfig
				),
				dao
		);
	}
}
