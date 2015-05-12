package com.mgs.mes.v3.reflections.domain;

import java.util.List;
import java.util.Map;

public interface Generics<T> {
	List<String> getListOfStrings();

	List<List<String>> getNestedStrings();

	List<T> getUnespecified();

	List<List<T>> getNestedUnespecified();

	T getIt ();

	Integer getInt ();

	int getIntPrimitive ();

	void getVoid ();

	Map<String, ExtendedGenerics<Integer>> getMap();
}
