package com.mgs.mes.v3;

import java.util.UUID;

public interface OneToOneMapEntity<T extends MapEntity> extends MapEntity{
	String getRefName ();
	UUID getRefId();

	T retrieve ();
}
