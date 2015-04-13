package com.mgs.mes.social.acquisition;

import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.social.item.Item;
import com.mgs.mes.social.person.Person;

import java.util.Date;

public interface AcquisitionBuilder extends EntityBuilder<Acquisition> {
	public AcquisitionBuilder withAcquirer(Person acquirer);
	public AcquisitionBuilder withItem(Item item);
	public AcquisitionBuilder withAcquiredDate (Date date);
}
