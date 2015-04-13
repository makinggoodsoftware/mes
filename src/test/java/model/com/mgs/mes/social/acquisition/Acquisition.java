package com.mgs.mes.social.acquisition;

import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityReference;
import com.mgs.mes.social.item.Item;
import com.mgs.mes.social.person.Person;

import java.util.Date;

public interface Acquisition extends Entity {
	public EntityReference<Person> getAcquirer();
	public EntityReference<Item> getItem();
	public Date getAcquiredDate ();
}
