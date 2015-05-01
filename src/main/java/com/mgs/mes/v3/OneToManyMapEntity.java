package com.mgs.mes.v3;

import java.util.List;

public interface OneToManyMapEntity<T extends MapEntity> extends MapEntity{
	List<OneToOneMapEntity<T>> getOneToOnes();

	List<T> retrieveAll();
}
