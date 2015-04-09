package com.mgs.mes.social.acquisition;

import com.mgs.mes.model.RelationshipBuilder;
import com.mgs.mes.social.item.Item;
import com.mgs.mes.social.person.Person;

import java.util.Date;

public interface AcquisitionBuilder extends RelationshipBuilder<Person, Item, Acquisition> {
	public AcquisitionBuilder withAcquiredDate (Date date);
}
