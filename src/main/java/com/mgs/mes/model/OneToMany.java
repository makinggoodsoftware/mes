package com.mgs.mes.model;

import java.util.List;

public interface OneToMany<T extends Entity> extends Entity{
	List<OneToOne<T>> getList();

	List<T> retrieveAll();
}
