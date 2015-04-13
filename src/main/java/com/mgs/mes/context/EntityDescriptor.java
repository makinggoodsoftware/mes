package com.mgs.mes.context;

import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;

public class EntityDescriptor <T extends Entity, Z extends EntityBuilder<T>>{
	private final Class<T> entityType;
	private final Class<Z> builderType;

	public EntityDescriptor(Class<T> entityType, Class<Z> builderType) {
		this.entityType = entityType;
		this.builderType = builderType;
	}

	public Class<T> getEntityType() {
		return entityType;
	}

	public Class<Z> getBuilderType() {
		return builderType;
	}

	@SuppressWarnings("RedundantIfStatement")
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EntityDescriptor)) return false;

		EntityDescriptor that = (EntityDescriptor) o;

		if (!builderType.equals(that.builderType)) return false;
		if (!entityType.equals(that.entityType)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = entityType.hashCode();
		result = 31 * result + builderType.hashCode();
		return result;
	}

	@SuppressWarnings({"StringBufferReplaceableByString", "StringBufferMayBeStringBuilder"})
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("EntityDescriptor{");
		sb.append("entityType=").append(entityType);
		sb.append(", builderType=").append(builderType);
		sb.append('}');
		return sb.toString();
	}
}
