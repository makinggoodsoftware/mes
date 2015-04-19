package com.mgs.mes.social.item;

import com.mgs.mes.model.Entity;
import com.mgs.mes.model.OneToMany;
import com.mgs.mes.social.color.Color;

@SuppressWarnings("UnusedDeclaration")
public interface Product extends Entity{
	public String getDescription ();
	public OneToMany<Color> getColors();
}
