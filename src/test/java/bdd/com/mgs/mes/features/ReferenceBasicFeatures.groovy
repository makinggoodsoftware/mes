package com.mgs.mes.features
import com.mgs.config.MesConfigFactory
import com.mgs.mes.context.EntityDescriptor
import com.mgs.mes.context.MongoContext
import com.mgs.mes.context.MongoManager
import com.mgs.mes.social.acquisition.Purchase
import com.mgs.mes.social.acquisition.PurchaseBuilder
import com.mgs.mes.social.color.Color
import com.mgs.mes.social.color.ColorBuilder
import com.mgs.mes.social.item.Product
import com.mgs.mes.social.item.ProductBuilder
import com.mgs.mes.social.person.Person
import com.mgs.mes.social.person.PersonBuilder
import spock.lang.Specification

import java.util.function.Function

public class ReferenceBasicFeatures extends Specification {
    MongoContext context
    EntityDescriptor<Person, PersonBuilder> personDescriptor = new EntityDescriptor<>(Person, PersonBuilder)
    EntityDescriptor<Product, ProductBuilder> itemDescriptor = new EntityDescriptor<>(Product, ProductBuilder)
    EntityDescriptor<Purchase, PurchaseBuilder> purchaseDescriptor = new EntityDescriptor<>(Purchase, PurchaseBuilder)
    EntityDescriptor<Color, ColorBuilder> colorDescriptor = new EntityDescriptor<>(Color, ColorBuilder)

    MongoManager<Person, PersonBuilder> persons
    MongoManager<Product, ProductBuilder> products
    MongoManager<Purchase, PurchaseBuilder> purchases
    MongoManager<Color, ColorBuilder> colors


    def "setup"() {
        context = new MesConfigFactory().
                simple("localhost", 27017, "bddDb").
                mongoContext([
                        personDescriptor,
                        itemDescriptor,
                        purchaseDescriptor,
                        colorDescriptor
                ]);

        persons = context.manager(personDescriptor)
        products = context.manager(itemDescriptor)
        purchases = context.manager(purchaseDescriptor)
        colors = context.manager(colorDescriptor)
    }

    def "should create references as relationships"() {
        when: "Creating the person, colors and product"
        //noinspection GroovyAssignabilityCheck
        Person alberto = persons.createAndPersist(functionClosure {PersonBuilder person ->
            person.withName("alberto")
        })


        //noinspection GroovyAssignabilityCheck
        Color yellow = colors.createAndPersist(functionClosure {ColorBuilder color ->
            color.withName("Yellow")
        })

        //noinspection GroovyAssignabilityCheck
        Color red = colors.createAndPersist(functionClosure {ColorBuilder color ->
            color.withName("Red")
        })

        //noinspection GroovyAssignabilityCheck
        colors.createAndPersist(functionClosure {ColorBuilder color ->
            color.withName("Black")
        })

        //noinspection GroovyAssignabilityCheck
        Product macBookPro = products.createAndPersist(functionClosure {ProductBuilder item ->
            item.
                withDescription("MacBook Pro 13''").
                withColors([yellow, red])
        })

        then:
        macBookPro.colors.getList () == [yellow, red]

        when: "saving the acquisition"
        Date acquiredDate = new Date()

        //noinspection GroovyAssignabilityCheck
        Purchase acquisition = purchases.createAndPersist(functionClosure {PurchaseBuilder acquisition -> acquisition.
            withAcquiredDate(acquiredDate).
            withBuyer(alberto).
            withProduct(macBookPro)
        })

        then:
        acquisition.buyer.retrieve() == alberto
        acquisition.product.retrieve() == macBookPro
        acquisition.acquiredDate == acquiredDate

        when: "retrieving the previously saved acquisition"
        Purchase acquisitionFromDb = purchases.retriever.byId(acquisition.getId().get()).get()

        then:
        acquisitionFromDb == acquisition
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
