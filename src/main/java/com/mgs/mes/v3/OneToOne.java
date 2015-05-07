package com.mgs.mes.v3;

import com.mgs.mes.v3.mapper.MapEntity;

public interface OneToOne<T extends MapEntity> extends Entity {
	String getRefName ();
	String getRefId();
	T retrieve ();
}
