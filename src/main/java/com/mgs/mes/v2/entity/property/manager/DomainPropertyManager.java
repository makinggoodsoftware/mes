package com.mgs.mes.v2.entity.property.manager;

import com.mgs.mes.model.Entity;

public interface DomainPropertyManager {
	boolean applies(Class<? extends Entity> parentType, String key);

	Object enrich(Class type, Object toEnrich);

}
