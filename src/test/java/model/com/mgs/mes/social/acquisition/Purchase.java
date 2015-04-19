package com.mgs.mes.social.acquisition;

import com.mgs.mes.model.Entity;
import com.mgs.mes.model.OneToOne;
import com.mgs.mes.social.item.Product;
import com.mgs.mes.social.person.Person;

import java.util.Date;

public interface Purchase extends Entity {
	public OneToOne<Person> getBuyer();
	public OneToOne<Product> getProduct();
	public Date getAcquiredDate ();
}
