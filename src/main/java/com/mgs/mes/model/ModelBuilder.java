package com.mgs.mes.model;

public interface ModelBuilder<T extends MongoEntity> {
	T create();
}
