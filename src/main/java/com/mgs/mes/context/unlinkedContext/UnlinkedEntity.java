package com.mgs.mes.context.unlinkedContext;

import com.mgs.mes.context.EntityDescriptor;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;

public class UnlinkedEntity<T extends Entity, Z extends EntityBuilder<T>> {
	private final EntityDescriptor<T, Z> entityDescriptor;

	public UnlinkedEntity(
			EntityDescriptor<T, Z> entityDescriptor) {
		this.entityDescriptor = entityDescriptor;
	}

	public EntityDescriptor<T, Z> getEntityDescriptor() {
		return entityDescriptor;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UnlinkedEntity)) return false;

		UnlinkedEntity that = (UnlinkedEntity) o;

		//noinspection RedundantIfStatement
		if (!entityDescriptor.equals(that.entityDescriptor)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		//noinspection UnnecessaryLocalVariable
		int result = entityDescriptor.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "UnlinkedEntity{entityDescriptor=" + entityDescriptor + '}';
	}
}
