package com.mgs.mes.context.unlinkedContext;

import com.mgs.mes.context.EntityDescriptor;
import com.mgs.mes.db.EntityRetriever;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;

public class UnlinkedEntity<T extends Entity, Z extends EntityBuilder<T>> {
	private final EntityRetriever<T> retriever;
	private final EntityDescriptor<T, Z> entityDescriptor;

	public UnlinkedEntity(
			EntityRetriever<T> retriever,
			EntityDescriptor<T, Z> entityDescriptor) {
		this.retriever = retriever;
		this.entityDescriptor = entityDescriptor;
	}

	public EntityRetriever<T> getRetriever() {
		return retriever;
	}

	public EntityDescriptor<T, Z> getEntityDescriptor() {
		return entityDescriptor;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UnlinkedEntity)) return false;

		UnlinkedEntity that = (UnlinkedEntity) o;

		if (!entityDescriptor.equals(that.entityDescriptor)) return false;
		//noinspection RedundantIfStatement
		if (!retriever.equals(that.retriever)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = retriever.hashCode();
		result = 31 * result + entityDescriptor.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "UnlinkedEntity{" + "retriever=" + retriever + ", entityDescriptor=" + entityDescriptor + '}';
	}
}
