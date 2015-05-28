package com.mgs.config.reflection;

import com.mgs.mes.v3.reflection.GenericsExpert;
import com.mgs.mes.v3.reflection.ParsedTypeFactory;
import com.mgs.mes.v4.typeParser.TypeParser;
import com.mgs.reflection.BeanNamingExpert;
import com.mgs.reflection.FieldAccessorParser;
import com.mgs.reflection.Reflections;

public class ReflectionConfig {
	public BeanNamingExpert beanNamingExpert() {
		return new BeanNamingExpert();
	}

	public FieldAccessorParser fieldAccessorParser() {
		return new FieldAccessorParser(beanNamingExpert(), new TypeParser());
	}

	public GenericsExpert genericsExpert() {
		return new GenericsExpert(new ParsedTypeFactory());
	}

	public Reflections reflections() {
		return new Reflections();
	}
}
