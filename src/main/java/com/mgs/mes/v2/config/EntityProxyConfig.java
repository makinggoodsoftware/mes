package com.mgs.mes.v2.config;

import com.mgs.mes.model.Entity;
import com.mgs.mes.model.OneToMany;
import com.mgs.mes.model.OneToOne;
import com.mgs.mes.v2.entity.EntityProxyFactory;
import com.mgs.mes.v2.entity.method.EntityMethodInterceptor;

import java.util.HashMap;
import java.util.Map;

public class EntityProxyConfig {
	private final ManagerConfig managerConfig;

	public EntityProxyConfig(ManagerConfig managerConfig) {
		this.managerConfig = managerConfig;
	}

	public EntityProxyFactory entityProxyFactory (){
		Map<Class<? extends Entity>, Map<String, EntityMethodInterceptor>> configuration = new HashMap<>();
		configuration.put(OneToOne.class, oneToOneLogic());
		configuration.put(OneToMany.class, oneToManyLogic());
		return new EntityProxyFactory(null, null, null);
	}

	private Map<String, EntityMethodInterceptor> oneToOneLogic() {
		Map<String, EntityMethodInterceptor> methods = new HashMap<>();
		methods.put("retrieve", (fromEntity, wrappedEntityType) -> managerConfig.oneToOne().onRetrieve((OneToOne) fromEntity));
		return methods;
	}

	private Map<String, EntityMethodInterceptor> oneToManyLogic() {
		Map<String, EntityMethodInterceptor> methods = new HashMap<>();
		methods.put("retrieveAll", (fromEntity, wrappedEntityType) -> managerConfig.oneToMany().onRetrieveAll((OneToMany<? extends Entity>) fromEntity));
		return methods;
	}
}
