package com.mgs.mes.v4;

@FunctionalInterface
public interface ObjectBuilderDataProvider<T> {
	void enrich(ObjectBuilder<T> initialized);
}
