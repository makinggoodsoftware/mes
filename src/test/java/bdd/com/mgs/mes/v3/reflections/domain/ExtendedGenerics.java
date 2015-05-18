package com.mgs.mes.v3.reflections.domain;

import java.util.List;

public interface ExtendedGenerics <T> {
	T getSimpleGenerics ();

	List<T> getListOfGenerics ();
}