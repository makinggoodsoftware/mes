package com.mgs.mes.v2.entity.property.manager;

public interface PropertyEnricher {
	Object enrich(Class type, Object toEnrich);
}
