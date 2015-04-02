package com.mgs.config.reflection;

import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mgs.reflection.Reflections;

public class ReflectionConfig {
	public BeanNamingExpert beanNamingExpert() {
		return new BeanNamingExpert();
	}

	public FieldAccessorParser fieldAccessorParser() {
		return new FieldAccessorParser(beanNamingExpert());
	}

	public Reflections reflections() {
		return new Reflections();
	}
}
