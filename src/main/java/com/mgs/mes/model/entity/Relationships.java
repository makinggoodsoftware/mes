package com.mgs.mes.model.entity;

public interface Relationships<T extends Entity> {
	Relationships<T> from (T from);
}
