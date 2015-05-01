package com.mgs.mes.v2.entity.method;

import com.mgs.mes.model.Entity;

import java.util.Optional;

public interface EntityMethodInterceptor<T extends Entity> {
	Object apply(T fromEntity, Optional<Class<? extends Entity>> wrappedEntityType);
}
