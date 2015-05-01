package com.mgs.mes.v2.entity.property.manager;

import com.mgs.mes.model.Entity;
import com.mgs.mes.model.OneToOne;
import com.mgs.mes.services.core.EntityRetriever;

import java.util.Map;

public class OneToOneManager {
	private final Map<String, EntityRetriever> retrievers;

	public OneToOneManager(Map<String, EntityRetriever> retrievers) {
		this.retrievers = retrievers;
	}

	public <T extends Entity> T onRetrieve(OneToOne<T> oneToOne) {
		//noinspection unchecked
		return (T) retrievers.get(oneToOne.getRefName()).byId(oneToOne.getRefId()).get();
	}
}
