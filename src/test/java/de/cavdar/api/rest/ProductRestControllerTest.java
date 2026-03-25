package de.cavdar.api.rest;

import de.cavdar.api.domain.Product;
import de.cavdar.api.domain.ProductInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * REST-Tests via RestClient (Spring Boot 4 – TestRestTemplate wurde entfernt).
 * defaultStatusHandler swallowed Fehlerstatus → toEntity() liefert immer ResponseEntity.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductRestControllerTest {

    @LocalServerPort
    int port;

    RestClient client;

    @BeforeEach
    void setUp() {
        client = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                // Fehler-Status nicht als Exception werfen → wir prüfen selbst
                .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> {})
                .build();
    }

    @Test
    void getAllProducts_returnsAtLeastInitialData() {
        ResponseEntity<Product[]> response = client.get()
                .uri("/api/products")
                .retrieve()
                .toEntity(Product[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void getProductById_returnsProduct() {
        ResponseEntity<Product> response = client.get()
                .uri("/api/products/1")
                .retrieve()
                .toEntity(Product.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Laptop");
    }

    @Test
    void getProductById_notFound_returns404() {
        ResponseEntity<String> response = client.get()
                .uri("/api/products/9999")
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createProduct_returnsCreatedProductWithId() {
        var input = new ProductInput("Monitor", "4K Display", 499.99);
        ResponseEntity<Product> response = client.post()
                .uri("/api/products")
                .body(input)
                .retrieve()
                .toEntity(Product.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Monitor");
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    void updateProduct_returnsUpdatedData() {
        // Eigenes Objekt anlegen → kein Konflikt mit anderen Tests
        var created = client.post()
                .uri("/api/products")
                .body(new ProductInput("TempRest", "temp", 1.0))
                .retrieve()
                .toEntity(Product.class)
                .getBody();
        assertThat(created).isNotNull();

        ResponseEntity<Product> response = client.put()
                .uri("/api/products/" + created.getId())
                .body(new ProductInput("TempRest Updated", "updated", 9.99))
                .retrieve()
                .toEntity(Product.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("TempRest Updated");
        assertThat(response.getBody().getPrice()).isEqualTo(9.99);
    }

    @Test
    void updateProduct_notFound_returns404() {
        ResponseEntity<String> response = client.put()
                .uri("/api/products/9999")
                .body(new ProductInput("X", "X", 1.0))
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteProduct_returns204_andIsGoneAfterwards() {
        // Eigenes Objekt anlegen und löschen
        var created = client.post()
                .uri("/api/products")
                .body(new ProductInput("ToDelete", "bye", 0.01))
                .retrieve()
                .toEntity(Product.class)
                .getBody();
        assertThat(created).isNotNull();

        ResponseEntity<Void> deleteResponse = client.delete()
                .uri("/api/products/" + created.getId())
                .retrieve()
                .toEntity(Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Nochmal holen → 404
        ResponseEntity<String> getResponse = client.get()
                .uri("/api/products/" + created.getId())
                .retrieve()
                .toEntity(String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteProduct_notFound_returns404() {
        ResponseEntity<Void> response = client.delete()
                .uri("/api/products/9999")
                .retrieve()
                .toEntity(Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
