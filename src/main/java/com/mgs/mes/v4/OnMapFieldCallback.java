package com.mgs.mes.v4;

import com.mgs.reflection.FieldAccessor;

@FunctionalInterface
public interface OnMapFieldCallback {
	void apply(FieldAccessor fieldAccessor, Object mapValue);
}
