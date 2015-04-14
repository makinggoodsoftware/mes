package com.mgs.config;

import com.mgs.config.mes.context.ContextConfig;
import com.mgs.config.mes.db.DatabaseConfig;
import com.mgs.config.mes.entity.EntityConfig;
import com.mgs.config.mes.meta.MetaConfig;
import com.mgs.config.mes.services.ServicesConfig;
import com.mgs.config.reflection.ReflectionConfig;
import com.mgs.mes.db.MongoDao;

public class MesConfigFactory {

	public MesConfig simple (String hostName, int port, String dbName){
		MetaConfig metaConfig = new MetaConfig(new ReflectionConfig());
		ReflectionConfig reflectionConfig = new ReflectionConfig();
		DatabaseConfig databaseConfig = new DatabaseConfig();
		MongoDao dao = databaseConfig.dao(hostName, port, dbName);
		return new MesConfig(
				new ContextConfig(
						new MetaConfig(reflectionConfig),
						new ServicesConfig(
								metaConfig,
								new EntityConfig(
										reflectionConfig
								),
								reflectionConfig
						)
				),
				dao
		);
	}
}
