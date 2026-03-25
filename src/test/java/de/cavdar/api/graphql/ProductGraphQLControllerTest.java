package de.cavdar.api.graphql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.ExecutionGraphQlService;
import org.springframework.graphql.test.tester.ExecutionGraphQlServiceTester;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GraphQL-Tests via ExecutionGraphQlServiceTester – testet die GraphQL-Schicht
 * direkt über den ExecutionGraphQlService, ohne HTTP-Stack.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ProductGraphQLControllerTest {

    @Autowired
    ExecutionGraphQlService graphQlService;

    ExecutionGraphQlServiceTester tester;

    @BeforeEach
    void setup() {
        tester = ExecutionGraphQlServiceTester.builder(graphQlService).build();
    }

    @Test
    void queryProducts_returnsData() {
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
                .path("products")
                .entityList(Object.class)
                .hasSizeGreaterThan(0);
    }

    @Test
    void queryProduct_byId_returnsCorrectName() {
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
                .path("product.name")
                .entity(String.class)
                .isEqualTo("Laptop");
    }

    @Test
    void queryProduct_notFound_returnsNull() {
        tester.document("""
                query {
                    product(id: "9999") {
                        id
                        name
                    }
                }
                """)
                .execute()
                .path("product")
                .valueIsNull();
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
                .path("createProduct.name")
                .entity(String.class)
                .isEqualTo("Headset");
    }

    @Test
    void mutationUpdateProduct_returnsUpdatedData() {
        // Erst neues Produkt anlegen
        var createResult = tester.document("""
                mutation {
                    createProduct(input: { name: "TempQL", description: "temp", price: 1.0 }) {
                        id
                    }
                }
                """)
                .execute()
                .path("createProduct.id")
                .entity(String.class)
                .get();

        tester.document("""
                mutation($id: ID!) {
                    updateProduct(id: $id, input: { name: "TempQL Updated", description: "x", price: 5.0 }) {
                        name
                        price
                    }
                }
                """)
                .variable("id", createResult)
                .execute()
                .path("updateProduct.name")
                .entity(String.class)
                .isEqualTo("TempQL Updated");
    }

    @Test
    void mutationDeleteProduct_returnsTrue() {
        // Erst neues Produkt anlegen, dann löschen
        var id = tester.document("""
                mutation {
                    createProduct(input: { name: "ToDeleteQL", description: "bye", price: 0.1 }) {
                        id
                    }
                }
                """)
                .execute()
                .path("createProduct.id")
                .entity(String.class)
                .get();

        tester.document("""
                mutation($id: ID!) {
                    deleteProduct(id: $id)
                }
                """)
                .variable("id", id)
                .execute()
                .path("deleteProduct")
                .entity(Boolean.class)
                .isEqualTo(true);
    }
}
