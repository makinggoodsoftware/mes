package com.mgs.mes.factory;

import com.mgs.mes.db.MongoDao;
import com.mgs.mes.db.MongoPersister;
import com.mgs.mes.db.MongoRetriever;
import com.mgs.mes.model.builder.EntityBuilderFactory;
import com.mgs.mes.model.builder.RelationshipBuilderFactory;
import com.mgs.mes.model.entity.*;
import com.mgs.mes.model.relationships.RelationshipsFactory;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class MongoContextFactory {
	private final MongoInternalDependencies mongoInternalDependencies;
	private final Map<Class<? extends Entity>, UnlinkedEntityDescriptor> descriptorsByEntity = new HashMap<>();
	private final MongoFactory mongoFactory;

	public MongoContextFactory(MongoInternalDependencies mongoInternalDependencies, MongoFactory mongoFactory) {
		this.mongoInternalDependencies = mongoInternalDependencies;
		this.mongoFactory = mongoFactory;
	}

	public static MongoContextFactory from(String host, String dbName, int port){
		try {
			MongoClient localhost = new MongoClient(host, port);
			DB db = localhost.getDB(dbName);
			MongoDao mongoDao = new MongoDao(db);
			MongoInternalDependencies dependencies = MongoInternalDependencies.init();
			MongoFactory mongoFactory = new MongoFactory(dependencies, mongoDao);
			return new MongoContextFactory(dependencies, mongoFactory);
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
	}

	public <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>>
	void register(Class<T> entityType, Class<Z> entityBuilderType, Class<Y> relationshipsType){
		if (descriptorsByEntity.get(entityType) != null) throw new IllegalStateException(String.format(
			"Trying to register an entity that has been already registered. Type : [%s]",
			entityType
		));
		this.mongoInternalDependencies.getValidator().validate(entityType, entityBuilderType);

		MongoRetriever<T> retriever = mongoFactory.retriever(entityType);
		MongoPersister<T, Z> persister = mongoFactory.persister(entityType, entityBuilderType);
		EntityBuilderFactory<T, Z> builder = mongoFactory.builder(entityType, entityBuilderType);

		descriptorsByEntity.put(
			entityType,
			new UnlinkedEntityDescriptor<>(retriever, persister, builder, relationshipsType, entityBuilderType)
		);
	}

	public MongoContext create (){
		return new MongoContext(buildManagersMap());
	}

	private Map<Class<? extends Entity>, MongoManager> buildManagersMap() {
		Map<Class<? extends RelationshipBuilder>, RelationshipBuilderFactory> relationshipBuilderFactoriesByType = buildRelationshipsMap(descriptorsByEntity);
		//noinspection unchecked
		return descriptorsByEntity.entrySet().stream()
				.collect(toMap(
						Map.Entry::getKey,
						(descriptorByEntity) -> createMongoManager(
								descriptorByEntity.getValue(),
								relationshipBuilderFactoriesByType
						)
				));
	}

	private <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>>
		MongoManager<T, Z, Y> createMongoManager(UnlinkedEntityDescriptor<T, Z, Y> unlinked, Map<Class<? extends RelationshipBuilder>, RelationshipBuilderFactory> relationshipBuilderFactoriesByType){
		RelationshipsFactory<T, Y> relationships = mongoFactory.relationships(unlinked.getRelationshipsType(), relationshipBuilderFactoriesByType);
		return new MongoManager<>(
				unlinked.getRetriever(),
				unlinked.getPersister(),
				unlinked.getBuilder(),
				relationships
		);
	}

	private Map<Class<? extends RelationshipBuilder>, RelationshipBuilderFactory> buildRelationshipsMap(Map<Class<? extends Entity>, UnlinkedEntityDescriptor> descriptorsByEntity) {
		//noinspection unchecked
		return descriptorsByEntity.entrySet().stream()
				.filter(descriptorByEntity -> descriptorByEntity.getKey().isAssignableFrom(Relationship.class))
				.collect(toMap(
						(descriptorByEntity) -> (Class<RelationshipBuilder>) descriptorByEntity.getValue().getBuilderType(),
						(descriptorByEntity) -> {
							UnlinkedEntityDescriptor unlinked = descriptorByEntity.getValue();
							return (RelationshipBuilderFactory) unlinked.getBuilder();
						}
				));
	}
}