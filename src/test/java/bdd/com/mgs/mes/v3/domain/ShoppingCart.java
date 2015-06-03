package com.mgs.mes.v3.domain;

import com.mgs.mes.v3.OneToMany;
import com.mgs.mes.v3.OneToOne;
import com.mgs.mes.v3.RootEntity;

public interface ShoppingCart extends RootEntity {
	OneToOne<User> getUser();
	OneToMany<Product> getProducts();
}