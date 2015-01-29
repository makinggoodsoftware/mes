package com.mgs.mes.orm;

import com.mongodb.DBObject;

import java.util.Map;

public class ModelData {
	private final DBObject dbo;
	private final Map<String, Object> fieldsByGetterMethodName;

	public ModelData(DBObject dbo, Map<String, Object> fieldsByGetterMethodName) {
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
		if (!(o instanceof ModelData)) return false;

		ModelData modelData = (ModelData) o;

		return dbo.equals(modelData.dbo) && fieldsByGetterMethodName.equals(modelData.fieldsByGetterMethodName);
	}

	@Override
	public int hashCode() {
		int result = dbo.hashCode();
		result = 31 * result + fieldsByGetterMethodName.hashCode();
		return result;
	}
}
