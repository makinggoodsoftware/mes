package com.mgs.mes.v4;

public class TwoStepsObjectFactory {
	public <T, Z extends ObjectBuilder<T>> T create (Class<? extends T> type, ObjectBuilderCreator<T, Z> creator, ObjectBuilderDataProvider<T> objectBuilderDataProvider){
		Z initialized = creator.create(type, objectBuilderDataProvider);
		objectBuilderDataProvider.enrich(initialized);
		return initialized.build();
	}
}
