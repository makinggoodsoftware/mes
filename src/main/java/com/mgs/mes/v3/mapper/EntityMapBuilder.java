package com.mgs.mes.v3.mapper;

@FunctionalInterface
public interface EntityMapBuilder<T> {
	T apply (T into);
}
