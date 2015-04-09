package com.mgs.mes.social.person;

import com.mgs.mes.model.EntityBuilder;

public interface PersonBuilder extends EntityBuilder<Person>{
	public PersonBuilder withName (String name);
}
