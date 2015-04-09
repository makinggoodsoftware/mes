package com.mgs.mes.social.person;

import com.mgs.mes.model.Relationships;
import com.mgs.mes.social.acquisition.AcquisitionBuilder;
import com.mgs.mes.social.item.Item;

public interface PersonRelationships extends Relationships<Person>{
	public AcquisitionBuilder acquire(Item item);
}
