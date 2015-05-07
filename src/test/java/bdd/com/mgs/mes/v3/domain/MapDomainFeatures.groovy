package com.mgs.mes.v3.domain
import com.mgs.config.reflection.ReflectionConfig
import com.mgs.mes.v3.mapper.MapEntityConfig
import com.mgs.mes.v3.mapper.MapEntityContext
import spock.lang.Specification

class MapDomainFeatures extends Specification {
    MapEntityConfig mapEntityConfig = new MapEntityConfig(new ReflectionConfig())
    MapEntityContext context

    def "setup" () {
        context = mapEntityConfig.contextFactory().defaultContext()
    }

    def "should create simple shopping cart" (){
        given:
        String productId = UUID.randomUUID().toString()
        String userId = UUID.randomUUID().toString()
        String shoppingCartId = UUID.randomUUID().toString()

        when: "Creating product"
        Product product = context.transform([
                id: productId,
                name: "MacBook Pro 13''"
            ],
            Product
        )

        then:
        product.id == productId
        product.name == "MacBook Pro 13''"
        ! product.version.isPresent()

        when: "Creating user"
        User user = context.transform([
                id: userId,
                firstName: "Alberto",
                lastName: "Gutierrez",
            ],
            User
        )

        then:
        user.id == userId
        user.firstName == "Alberto"
        user.lastName == "Gutierrez"
        ! user.version.isPresent()

        when: "Creating shopping cart"
        ShoppingCart shoppingCart = context.transform([
                    id: shoppingCartId,
                    user: [
                            refName : "User",
                            refId : userId
                    ],
                    products: [relationships:[
                        [
                            refName: Product,
                            refId: productId
                        ],
                    ]]
                ],
                ShoppingCart
        )

        then:
        shoppingCart.id == userId
        shoppingCart.user.refId == userId
        shoppingCart.user.refName == "User"
        shoppingCart.products.relationships.size() == 1
        shoppingCart.products.relationships.get(0).refId == productId
        shoppingCart.products.relationships.get(0).refName == "Product"
        ! shoppingCart.version.isPresent()
    }
}
