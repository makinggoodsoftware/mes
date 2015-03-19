package com.mgs.mes.meta.init;

import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.model.Relationship;
import com.mgs.mes.model.Relationships;

public class EntityDescriptor <T extends Entity, Z extends EntityBuilder<T>, Y extends Relationships<T>>{
	private final Class<T> entityType;
	private final Class<Z> builderType;
	private final Class<Y> relationshipsType;

	public EntityDescriptor(Class<T> entityType, Class<Z> builderType, Class<Y> relationshipsType) {
		this.entityType = entityType;
		this.builderType = builderType;
		this.relationshipsType = relationshipsType;
	}

	public Class<T> getEntityType() {
		return entityType;
	}

	public Class<Z> getBuilderType() {
		return builderType;
	}

	public Class<Y> getRelationshipsType() {
		return relationshipsType;
	}

	public boolean isRelationshipEntity (){
		return Relationship.class.isAssignableFrom(entityType);
	}

	@SuppressWarnings("RedundantIfStatement")
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EntityDescriptor)) return false;

		EntityDescriptor that = (EntityDescriptor) o;

		if (!builderType.equals(that.builderType)) return false;
		if (!entityType.equals(that.entityType)) return false;
		if (!relationshipsType.equals(that.relationshipsType)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = entityType.hashCode();
		result = 31 * result + builderType.hashCode();
		result = 31 * result + relationshipsType.hashCode();
		return result;
	}

	@SuppressWarnings({"StringBufferReplaceableByString", "StringBufferMayBeStringBuilder"})
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("EntityDescriptor{");
		sb.append("entityType=").append(entityType);
		sb.append(", builderType=").append(builderType);
		sb.append(", relationshipsType=").append(relationshipsType);
		sb.append('}');
		return sb.toString();
	}
}
