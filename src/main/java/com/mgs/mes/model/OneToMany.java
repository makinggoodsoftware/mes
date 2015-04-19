package com.mgs.mes.model;

import java.util.List;

public interface OneToMany<T extends Entity> extends Entity{
	public List<T> asList();
}
