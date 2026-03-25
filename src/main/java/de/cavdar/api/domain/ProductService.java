package de.cavdar.api.domain;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Gemeinsame Business-Logik – alle drei API-Schichten nutzen diesen Service.
 * Zeigt: Die Domäne ist API-agnostisch.
 */
@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return repository.findById(id);
    }

    public Product create(ProductInput input) {
        return repository.save(input);
    }

    public Optional<Product> update(Long id, ProductInput input) {
        return repository.update(id, input);
    }

    public boolean delete(Long id) {
        return repository.deleteById(id);
    }
}
