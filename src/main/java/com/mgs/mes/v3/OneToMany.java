package com.mgs.mes.v3;

import com.mgs.mes.v3.mapper.MapEntity;

import java.util.List;

public interface OneToMany<T extends MapEntity> extends Entity {
	List<OneToOne<T>> getRelationships();

	List<T> retrieveAll();
}
