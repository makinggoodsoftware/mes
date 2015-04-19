package com.mgs.mes.social.item;

import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.social.color.Color;

import java.util.List;

public interface ProductBuilder extends EntityBuilder<Product>{
	public ProductBuilder withDescription(String description);
	public ProductBuilder withColors(List<Color> colors);
}
