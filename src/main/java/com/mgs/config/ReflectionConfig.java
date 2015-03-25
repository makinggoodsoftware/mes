package com.mgs.config;

import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;

public class ReflectionConfig {
	public BeanNamingExpert beanNamingExpert() {
		return new BeanNamingExpert();
	}

	public FieldAccessorParser fieldAccessorParser() {
		return new FieldAccessorParser(beanNamingExpert());
	}
}
