package com.mgs.mes.social.acquisition;

import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.social.item.Product;
import com.mgs.mes.social.person.Person;

import java.util.Date;

public interface PurchaseBuilder extends EntityBuilder<Purchase> {
	public PurchaseBuilder withBuyer(Person acquirer);
	public PurchaseBuilder withProduct(Product product);
	public PurchaseBuilder withAcquiredDate (Date date);
}
