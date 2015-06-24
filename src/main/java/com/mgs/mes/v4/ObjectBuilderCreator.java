package com.mgs.mes.v4;

@FunctionalInterface
public interface ObjectBuilderCreator<T, Z extends ObjectBuilder<T>> {
	Z create (Class<? extends T> type, ObjectBuilderDataProvider<T> objectBuilderDataProvider);
}
