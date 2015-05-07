package com.mgs.mes.v3;

import com.mgs.mes.v3.mapper.MapEntity;
import com.mgs.mes.v3.mapper.Parametrizable;

import java.util.List;

public interface OneToMany<T extends MapEntity> extends Entity {
	@Parametrizable
	List<OneToOne<T>> getRelationships();

	List<T> retrieveAll();
}
