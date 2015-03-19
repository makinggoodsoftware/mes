package com.mgs.mes.model;

public interface Relationships<T extends Entity> {
	Relationships<T> from (T from);
}
