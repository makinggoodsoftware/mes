package com.mgs.mes.v2.property.manager;

import com.mgs.mes.model.Entity;
import com.mgs.mes.v2.property.type.dbo.DboPropertyType;

public interface DomainPropertyManager {
	boolean applies(Class<? extends Entity> parentType, String key);

	Object enrich(DboPropertyType dboPropertyType, Object toEnrich);

}
