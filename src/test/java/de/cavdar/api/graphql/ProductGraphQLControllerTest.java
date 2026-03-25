package de.cavdar.api.graphql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

@SpringBootTest
@AutoConfigureHttpGraphQlTester
class ProductGraphQLControllerTest {

    @Autowired
    HttpGraphQlTester tester;

    @Test
    void queryProducts_returnsInitialDemoData() {
        tester.document("""
                query {
                    products {
                        id
                        name
                        price
                    }
                }
                """)
                .execute()
                .path("products").entityList(Object.class).hasSize(3);
    }

    @Test
    void queryProduct_byId_returnsProduct() {
        tester.document("""
                query {
                    product(id: "1") {
                        id
                        name
                        description
                        price
                    }
                }
                """)
                .execute()
                .path("product.name").entity(String.class).isEqualTo("Laptop");
    }

    @Test
    void mutationCreateProduct_returnsNewProduct() {
        tester.document("""
                mutation {
                    createProduct(input: {
                        name: "Headset"
                        description: "Noise cancelling"
                        price: 199.99
                    }) {
                        id
                        name
                        price
                    }
                }
                """)
                .execute()
                .path("createProduct.name").entity(String.class).isEqualTo("Headset");
    }

    @Test
    void mutationDeleteProduct_returnsTrue() {
        tester.document("""
                mutation {
                    deleteProduct(id: "3")
                }
                """)
                .execute()
                .path("deleteProduct").entity(Boolean.class).isEqualTo(true);
    }
}
