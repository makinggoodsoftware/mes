package com.mgs.mes.v3.mapper;

import java.lang.reflect.Method;
import java.util.Optional;

public interface MapEntityManager<T extends MapEntity> {
	Class getSupportedType();

	Optional<EntityMethod<T>> applies(Method method);
}
