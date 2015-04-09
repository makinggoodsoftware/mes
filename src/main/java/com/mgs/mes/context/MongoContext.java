package com.mgs.mes.context;

import com.mgs.mes.build.factory.builder.RelationshipBuilderFactory;
import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.model.*;

import java.util.Map;

import static java.util.stream.Collectors.toList;

public class MongoContext {
	private final Map<EntityDescriptor, MongoManager> managersByEntity;
	private final Map<Class<? extends RelationshipBuilder>, RelationshipBuilderFactory> modelBuildersByType;

	public MongoContext(Map<EntityDescriptor, MongoManager> managersByEntity, Map<Class<? extends RelationshipBuilder>, RelationshipBuilderFactory> modelBuildersByType) {
		this.managersByEntity = managersByEntity;
		this.modelBuildersByType = modelBuildersByType;
	}

	public <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>>
	MongoManager<T, Z, Y> manager (EntityDescriptor<T, Z, Y> entityDescriptor) {
		//noinspection unchecked
		return managersByEntity.get(entityDescriptor);
	}

	public <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>>
	MongoManager<T, Z, Y> manager (Class<T> entityType) {
		//noinspection unchecked
		return managersByEntity.entrySet().stream().
				filter((entry)->entry.getKey().getEntityType() == entityType).
				map(Map.Entry::getValue).
				collect(toList()).
				get(0);
	}

	public <A extends Entity, B extends Entity, T extends Relationship<A, B>, Z extends RelationshipBuilder<A, B, T> >
	RelationshipBuilderFactory <A, B, T, Z> getRelationshipBuilderFactory (Class<? extends RelationshipBuilder<A, B, T>> ofType){
		//noinspection unchecked
		return modelBuildersByType.get(ofType);
	}

	public <T extends Entity> EntityRetriever<T> getRetriever(Class<T> entityType) {
		return manager(entityType).getRetriever();
	}

	@SuppressWarnings("RedundantIfStatement")
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MongoContext)) return false;

		MongoContext that = (MongoContext) o;

		if (!managersByEntity.equals(that.managersByEntity)) return false;
		if (!modelBuildersByType.equals(that.modelBuildersByType)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = managersByEntity.hashCode();
		result = 31 * result + modelBuildersByType.hashCode();
		return result;
	}
}
