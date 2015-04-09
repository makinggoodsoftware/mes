package com.mgs.mes.social.acquisition;

import com.mgs.mes.model.Relationship;
import com.mgs.mes.social.item.Item;
import com.mgs.mes.social.person.Person;

import java.util.Date;

public interface Acquisition extends Relationship<Person, Item>{
	public Date getAcquiredDate ();
}
