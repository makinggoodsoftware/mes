package com.mgs.mes.v4;


import com.mgs.mes.v3.mapper.MapEntity;

@FunctionalInterface
public interface EntityMapListBuilderCaller<T extends MapEntity> {
	EntityMapListBuilder<T> apply (EntityMapListBuilder<T> builder);
}
