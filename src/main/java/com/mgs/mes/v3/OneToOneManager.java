package com.mgs.mes.v3;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Method;
import java.util.Optional;

public class OneToOneManager implements MapEntityManager<OneToOneMapEntity<? extends MapEntity>>{
	private final MapEntityContext mapEntityContext;
	private final MapRetrieverLocator mapRetrieverLocator;

	public OneToOneManager(MapRetrieverLocator mapRetrieverLocator, MapEntityContext mapEntityContext) {
		this.mapRetrieverLocator = mapRetrieverLocator;
		this.mapEntityContext = mapEntityContext;
	}

	@Override
	public Class<OneToOneMapEntity> getSupportedType() {
		return OneToOneMapEntity.class;
	}

	@Override
	public Optional<EntityMethod<OneToOneMapEntity<? extends MapEntity>>> applies(Method method) {
		return Optional.empty();
	}

	public EntityMethod<OneToOneMapEntity> onRetrieve(){
		//noinspection unchecked
		return (type, on, map, params) -> retrieve(type, on);
	}

	private <T extends MapEntity> T retrieve(Class<? extends OneToOneMapEntity> type, OneToOneMapEntity<T> from) {
//		Optional<Retriever> retriever = mapRetrieverLocator.byName(from.getRefName());
//		if (! retriever.isPresent()) throw new IllegalStateException();
//
//		Map<String, Object> map = retriever.get().asMap(from.getRefId());
//		return mapEntityContext.transform(map, (Class<MapEntity>) type);
		throw new NotImplementedException();
	}

}