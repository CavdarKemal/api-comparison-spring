package de.cavdar.api.graphql;

import de.cavdar.api.domain.Product;
import de.cavdar.api.domain.ProductInput;
import de.cavdar.api.domain.ProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL API – Schema-first, ein einziger Endpunkt (/graphql).
 *
 * Schema: src/main/resources/graphql/schema.graphqls
 * GraphiQL UI: http://localhost:8080/graphiql
 *
 * Charakteristika:
 *  - Client bestimmt GENAU welche Felder er braucht (kein Over-/Underfetching)
 *  - Queries (Lesen) und Mutations (Schreiben) klar getrennt
 *  - Typsystem im Schema definiert – self-documenting
 *  - Ein einziger HTTP-Endpunkt für alle Operationen
 *  - Ideal für flexible Frontends mit wechselnden Datenanforderungen
 */
@Controller
public class ProductGraphQLController {

    private final ProductService service;

    public ProductGraphQLController(ProductService service) {
        this.service = service;
    }

    // ===== Queries =====

    @QueryMapping
    public List<Product> products() {
        return service.findAll();
    }

    @QueryMapping
    public Product product(@Argument Long id) {
        return service.findById(id).orElse(null);
    }

    // ===== Mutations =====

    @MutationMapping
    public Product createProduct(@Argument ProductInput input) {
        return service.create(input);
    }

    @MutationMapping
    public Product updateProduct(@Argument Long id, @Argument ProductInput input) {
        return service.update(id, input).orElse(null);
    }

    @MutationMapping
    public boolean deleteProduct(@Argument Long id) {
        return service.delete(id);
    }
}
