package com.mgs.mes.v3.domain
import com.mgs.config.reflection.ReflectionConfig
import com.mgs.mes.v3.OneToOne
import com.mgs.mes.v3.mapper.*
import com.mgs.mes.v4.EntityMapListBuilder
import com.mgs.mes.v4.EntityMapListBuilderCaller
import spock.lang.Specification

import java.lang.reflect.Method

import static java.util.Optional.empty
import static java.util.Optional.of

class MapDomainFeatures extends Specification {
    MapEntityConfig mapEntityConfig = new MapEntityConfig(new ReflectionConfig())
    MapEntityContext context
    User user

    def "setup" () {
        context = mapEntityConfig.contextFactory().withManagers(new MapEntityManager() {
            @Override
            Integer getInheritanceLevel() {
                return 1
            }

            @Override
            Class getSupportedType() {
                return OneToOne
            }

            @Override
            Optional<EntityMethod> applies(Method method) {
                EntityMethod<MapEntity> mapEntityEntityMethod = rawApplies(method)
                return mapEntityEntityMethod == null ? empty() : of(mapEntityEntityMethod)
            }

            private EntityMethod<MapEntity> rawApplies(Method method) {
                switch (method.getName()) {
                    case "retrieve":
                        return { MapEntity value, EntityMapCallInterceptor<MapEntity> interceptor, Object[] params ->
                            return user
                        } as EntityMethod
                }

                return null;
            }
        })
    }

    def "should create simple shopping cart from map" (){
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
        user = context.transform([
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
                            refName: "Product",
                            refId: productId
                        ],
                    ]]
                ],
                ShoppingCart
        )

        then:
        shoppingCart.id == shoppingCartId
        shoppingCart.user.refId == userId
        shoppingCart.user.refName == "User"
        shoppingCart.products.relationships.size() == 1
        shoppingCart.products.relationships.get(0).refId == productId
        shoppingCart.products.relationships.get(0).refName == "Product"
        ! shoppingCart.version.isPresent()

        when: "Retrieving user"
        def userFromShoppingCart = shoppingCart.user.retrieve().getDomainMap()

        then:
        userFromShoppingCart == [
                id: userId,
                firstName: "Alberto",
                lastName: "Gutierrez",
                version: Optional.empty()
        ]
    }

    def "should create simple shopping cart with builders" (){
        when:
        ShoppingCart shoppingCart = context.newEntity(ShoppingCart, { shoppingCart ->
            shoppingCart.
                withUser({ User user -> user.
                    withFirstName("Alberto").
                    withLastName("Gutierrez")
                } as EntityMapBuilder).
                withProducts({ EntityMapListBuilder<Product> builder -> builder.
                    with({ Product product1 -> product1.
                        withName("Mac book pro 15''")
                    } as EntityMapBuilder).
                    with({ Product product2 -> product2.
                        withName("Nexus 6")
                    } as EntityMapBuilder)
                } as EntityMapListBuilderCaller)
        } as EntityMapBuilder)

        then:
        shoppingCart.user.retrieve().firstName == "Alberto"
    }
}
