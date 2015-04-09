package com.mgs.mes.social.item;

import com.mgs.mes.model.EntityBuilder;

public interface ItemBuilder extends EntityBuilder<Item>{
	public ItemBuilder withDescription(String description);
}
