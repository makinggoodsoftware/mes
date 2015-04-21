package com.mgs.mes.model;

import java.util.List;

public interface OneToMany<T extends Entity> extends Entity{
	public List<OneToOne<T>> getList();

	public List<T> retrieveAll();
}
