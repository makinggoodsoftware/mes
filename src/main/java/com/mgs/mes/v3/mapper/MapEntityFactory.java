package com.mgs.mes.v3.mapper;

import com.google.common.collect.ImmutableMap;
import com.mgs.mes.v4.typeParser.ParsedType;
import com.mgs.mes.v4.typeParser.TypeParser;
import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.FieldAccessorParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.stream.Collectors.toMap;

public class MapEntityFactory {
	private final TypeParser typeParser;
	private final FieldAccessorParser fieldAccessorParser;

	public MapEntityFactory(TypeParser typeParser, FieldAccessorParser fieldAccessorParser) {
		this.typeParser = typeParser;
		this.fieldAccessorParser = fieldAccessorParser;
	}

	public <T extends MapEntity> T newEntity(
			Class type,
			List<MapEntityManager<T>> entityManagers,
			EntityMapBuilder<T> entityMapBuilder
	) {
		ParsedType parsedType = typeParser.parse(type);
		return newEntity(parsedType, entityManagers, entityMapBuilder);
	}

	public <T extends MapEntity> T newEntity(
			ParsedType parsedType,
			List<MapEntityManager<T>> entityManagers,
			EntityMapBuilder<T> entityMapBuilder
	) {
		Map<String, FieldAccessor> fieldAccessors = accessors(parsedType);
		return newEntity(parsedType, fieldAccessors, entityManagers, entityMapBuilder);
	}

	public <T extends MapEntity> T newEntity(
			ParsedType parsedType,
			Map<String, FieldAccessor> fieldAccessors,
			List<MapEntityManager<T>> entityManagers,
			EntityMapBuilder<T> entityMapBuilder
	) {
		Class actualType = parsedType.getOwnDeclaration().getTypeResolution().getSpecificClass().get();
		T emptyEntity = create(
				parsedType,
				actualType,
				entityManagers,
				fieldAccessors,
				new HashMap<>(),
				true
		);
		T modifiableEntity = entityMapBuilder.apply(emptyEntity);
		return create(
				parsedType,
				actualType,
				entityManagers,
				fieldAccessors,
				ImmutableMap.copyOf(modifiableEntity.asMap()),
				false
		);
	}

	public <T extends MapEntity> T fromMap(
			Class type,
			List<MapEntityManager<T>> entityManagers,
			Map<String, Object> objectValues
	) {
		ParsedType parsedType = typeParser.parse(type);
		return fromMap(parsedType, entityManagers, objectValues);
	}

	public <T extends MapEntity> T fromMap(
			ParsedType parsedType,
			List<MapEntityManager<T>> entityManagers,
			Map<String, Object> objectValues
	) {
		Map<String, FieldAccessor> fieldAccessors = accessors(parsedType);
		return fromMap(parsedType, fieldAccessors, entityManagers, objectValues);
	}

	public <T extends MapEntity> T fromMap(
			ParsedType parsedType,
			Map<String, FieldAccessor> fieldAccessors,
			List<MapEntityManager<T>> entityManagers,
			Map<String, Object> objectValues
	) {
		Class actualType = parsedType.getOwnDeclaration().getTypeResolution().getSpecificClass().get();

		return create(
				parsedType,
				actualType,
				entityManagers,
				fieldAccessors,
				objectValues,
				false
		);
	}

	private Map<String, FieldAccessor> accessors(ParsedType parsedType) {
		return fieldAccessorParser.parse(parsedType).collect(toMap(
						FieldAccessor::getMethodName,
						(parsedMethod) -> parsedMethod)
		);
	}

	private <T extends MapEntity> T create(ParsedType parsedType, Class actualType, List<MapEntityManager<T>> entityManagers, Map<String, FieldAccessor> fieldAccessors, Map<String, Object> domainMap, boolean modifiable) {
		//noinspection unchecked
		return (T) newProxyInstance(
				MapEntityContext.class.getClassLoader(),
				new Class[]{actualType},
				new EntityMapCallInterceptor<>(
						this,
						parsedType,
						domainMap,
						entityManagers,
						fieldAccessors,
						modifiable)
		);
	}
}
