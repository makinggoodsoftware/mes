package com.mgs.mes.social.color;

import com.mgs.mes.model.EntityBuilder;

public interface ColorBuilder extends EntityBuilder<Color>{
	public ColorBuilder withName(String description);
}
