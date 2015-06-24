package com.mgs.mes.v3.domain;

import com.mgs.mes.v3.OneToMany;
import com.mgs.mes.v3.OneToOne;
import com.mgs.mes.v3.RootEntity;
import com.mgs.mes.v3.mapper.EntityMapBuilder;
import com.mgs.mes.v4.EntityMapListBuilderCaller;

public interface ShoppingCart extends RootEntity {
	OneToOne<User> getUser();
	OneToMany<Product> getProducts();

	ShoppingCart withUser(EntityMapBuilder<User> user);
	ShoppingCart withProducts (EntityMapListBuilderCaller<Product> products);
}