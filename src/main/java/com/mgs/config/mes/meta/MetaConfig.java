package com.mgs.config.mes.meta;

import com.mgs.config.reflection.ReflectionConfig;
import com.mgs.mes.meta.utils.Entities;
import com.mgs.mes.meta.utils.Validator;

public class MetaConfig {
	private final ReflectionConfig reflectionConfig;

	public MetaConfig(ReflectionConfig reflectionConfig) {
		this.reflectionConfig = reflectionConfig;
	}

	public Entities entities() {
		return new Entities();
	}

	public Validator validator() {
		return new Validator(reflectionConfig.reflections(), reflectionConfig.fieldAccessorParser());
	}
}
