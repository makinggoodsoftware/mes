package com.mgs.mes.model.data;

import com.mongodb.DBObject;

import java.util.Map;

public class EntityData {
	private final DBObject dbo;
	private final Map<String, Object> fieldsByGetterMethodName;

	public EntityData(DBObject dbo, Map<String, Object> fieldsByGetterMethodName) {
		this.dbo = dbo;
		this.fieldsByGetterMethodName = fieldsByGetterMethodName;
	}

	public DBObject getDbo() {
		return dbo;
	}

	public Object get(String methodName) {
		return fieldsByGetterMethodName.get(methodName);
	}

	public boolean exists(Object getterName) {
		//noinspection SuspiciousMethodCalls
		return fieldsByGetterMethodName.containsKey(getterName);
	}

	@Override
	public String toString() {
		return "ModelData{" + "dbo=" + dbo + ", fieldsByGetterMethodName=" + fieldsByGetterMethodName + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EntityData)) return false;

		EntityData entityData = (EntityData) o;

		return dbo.equals(entityData.dbo) && fieldsByGetterMethodName.equals(entityData.fieldsByGetterMethodName);
	}

	@Override
	public int hashCode() {
		int result = dbo.hashCode();
		result = 31 * result + fieldsByGetterMethodName.hashCode();
		return result;
	}
}
