package com.mgs.mes.v3;

import java.util.Optional;

public interface RootEntity extends Entity {
	String getId();

	Optional<Integer> getVersion();
}
