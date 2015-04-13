package com.mgs.mes.features
import com.mgs.config.MesConfigFactory
import com.mgs.mes.context.EntityDescriptor
import com.mgs.mes.context.MongoContext
import com.mgs.mes.context.MongoManager
import com.mgs.mes.social.acquisition.Acquisition
import com.mgs.mes.social.acquisition.AcquisitionBuilder
import com.mgs.mes.social.item.Item
import com.mgs.mes.social.item.ItemBuilder
import com.mgs.mes.social.person.Person
import com.mgs.mes.social.person.PersonBuilder
import spock.lang.Specification

import java.util.function.Function

public class RelationshipFeatures extends Specification {
    MongoContext context
    EntityDescriptor<Person, PersonBuilder> personDescriptor = new EntityDescriptor<>(Person, PersonBuilder)
    EntityDescriptor<Item, ItemBuilder> itemDescriptor = new EntityDescriptor<>(Item, ItemBuilder)
    EntityDescriptor<Acquisition, AcquisitionBuilder> acquireDescriptor = new EntityDescriptor<>(Acquisition, AcquisitionBuilder)

    MongoManager<Person, PersonBuilder> persons
    MongoManager<Item, ItemBuilder> items
    MongoManager<Acquisition, AcquisitionBuilder> acquires


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

    def "should create references as relationships"() {
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
        Acquisition acquisition = acquires.newEntity()
            .withAcquiredDate(acquiredDate)
            .withAcquirer(alberto)
            .withItem(macBookPro)
            .create()

        then:
        acquisition.acquirer.retrieve() == alberto
        acquisition.item.retrieve() == macBookPro
        acquisition.acquiredDate == acquiredDate
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
