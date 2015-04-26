package com.mgs.mes.v2.property.manager.impl;

import com.mgs.mes.model.Entity;
import com.mgs.mes.v2.property.manager.DomainPropertyManager;
import com.mgs.mes.v2.property.type.dbo.DboPropertyType;
import com.mongodb.DBObject;

public class ValuePropertyManager implements DomainPropertyManager {
	@Override
	public boolean applies(Class<? extends Entity> parentType, String key) {
		return false;
	}

	@Override
	public Object enrich(DboPropertyType dboPropertyType, Object toEnrich) {
		return null;
	}

	@Override
	public Class<Entity> findEntityType(Class<? extends Entity> parentType, String key, DBObject asDbo) {
		return null;
	}
}
