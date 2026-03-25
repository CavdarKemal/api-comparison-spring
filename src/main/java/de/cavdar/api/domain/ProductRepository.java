package de.cavdar.api.domain;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-Memory Repository mit ConcurrentHashMap.
 * Kein JPA/DB nötig – Fokus liegt auf dem API-Vergleich.
 */
@Repository
public class ProductRepository {

    private final Map<Long, Product> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public ProductRepository() {
        // Demo-Daten beim Start
        save(new ProductInput("Laptop", "High-performance laptop", 1299.99));
        save(new ProductInput("Mouse", "Wireless ergonomic mouse", 49.99));
        save(new ProductInput("Keyboard", "Mechanical keyboard", 129.99));
    }

    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Product save(ProductInput input) {
        Long id = idGenerator.getAndIncrement();
        Product product = new Product(id, input.name(), input.description(), input.price());
        store.put(id, product);
        return product;
    }

    public Optional<Product> update(Long id, ProductInput input) {
        if (!store.containsKey(id)) return Optional.empty();
        Product updated = new Product(id, input.name(), input.description(), input.price());
        store.put(id, updated);
        return Optional.of(updated);
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }
}
