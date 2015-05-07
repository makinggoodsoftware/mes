package com.mgs.mes.v3;

import com.mgs.mes.v3.mapper.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Method;
import java.util.Optional;

public class OneToOneManager implements MapEntityManager<OneToOne<? extends MapEntity>> {
	private final MapEntityContext mapEntityContext;
	private final MapRetrieverLocator mapRetrieverLocator;

	public OneToOneManager(MapRetrieverLocator mapRetrieverLocator, MapEntityContext mapEntityContext) {
		this.mapRetrieverLocator = mapRetrieverLocator;
		this.mapEntityContext = mapEntityContext;
	}

	@Override
	public Class<OneToOne> getSupportedType() {
		return OneToOne.class;
	}

	@Override
	public Optional<EntityMethod<OneToOne<? extends MapEntity>>> applies(Method method) {
		return Optional.empty();
	}

	public EntityMethod<OneToOne> onRetrieve(){
		//noinspection unchecked
		return (type, on, map, params) -> retrieve(type, on);
	}

	private <T extends MapEntity> T retrieve(Class<? extends OneToOne> type, OneToOne<T> from) {
//		Optional<Retriever> retriever = mapRetrieverLocator.byName(from.getRefName());
//		if (! retriever.isPresent()) throw new IllegalStateException();
//
//		Map<String, Object> map = retriever.get().asMap(from.getRefId());
//		return mapEntityContext.transform(map, (Class<MapEntity>) type);
		throw new NotImplementedException();
	}

}