package com.mgs.mes.features

import com.mgs.config.MesConfigFactory
import com.mgs.mes.context.EntityDescriptor
import com.mgs.mes.context.MongoContext
import com.mgs.mes.context.MongoManager
import com.mgs.mes.social.acquisition.Acquisition
import com.mgs.mes.social.acquisition.AcquisitionBuilder
import com.mgs.mes.social.acquisition.AcquisitionRelationships
import com.mgs.mes.social.item.Item
import com.mgs.mes.social.item.ItemBuilder
import com.mgs.mes.social.item.ItemRelationships
import com.mgs.mes.social.person.Person
import com.mgs.mes.social.person.PersonBuilder
import com.mgs.mes.social.person.PersonRelationships
import spock.lang.Specification

import java.util.function.Function

public class RelationshipFeatures extends Specification {
    MongoContext context
    EntityDescriptor<Person, PersonBuilder, PersonRelationships> personDescriptor = new EntityDescriptor<>(Person, PersonBuilder, PersonRelationships)
    EntityDescriptor<Item, ItemBuilder, ItemRelationships> itemDescriptor = new EntityDescriptor<>(Item, ItemBuilder, ItemRelationships)
    EntityDescriptor<Acquisition, AcquisitionBuilder, AcquisitionRelationships> acquireDescriptor = new EntityDescriptor<>(Acquisition, AcquisitionBuilder, AcquisitionRelationships)

    MongoManager<Person, PersonBuilder, PersonRelationships> persons
    MongoManager<Item, ItemBuilder, ItemRelationships> items
    MongoManager<Acquisition, AcquisitionBuilder, AcquisitionRelationships> acquires


    def "setup"() {
        context = new MesConfigFactory().
                simple("localhost", 27017, "bddDb").
                mongoContext([
                        personDescriptor,
                        itemDescriptor,
                        acquireDescriptor
                ]);

        persons = context.manager(personDescriptor)
        items = context.manager(itemDescriptor)
        acquires = context.manager(acquireDescriptor)
    }

    def "a relationship could have additional attributes"() {
        given:
        //noinspection GroovyAssignabilityCheck
        Person alberto = persons.createAndPersist(functionClosure {PersonBuilder person ->
            person.withName("alberto")
        })

        //noinspection GroovyAssignabilityCheck
        Item macBookPro = items.createAndPersist(functionClosure {ItemBuilder item ->
            item.withDescription("MacBook Pro 13''")
        })

        Date acquiredDate = new Date()

        when:
        //noinspection GroovyAssignabilityCheck
        Acquisition acquire = acquires.persister.create(
            persons.newRelationshipFrom(alberto).
                    acquire(macBookPro).
                    withAcquiredDate(acquiredDate)
        )

        then:
        acquire.left.retrieve() == alberto
        acquire.right.retrieve() == macBookPro
        acquire.acquiredDate == acquiredDate
    }

    def functionClosure (groovyClosure){
        return new Function<Object, Object>() {
            @Override
            Object apply(Object builder) {
                 return groovyClosure(builder)
            }
        }
    }
}
