package de.cavdar.api.rest;

import de.cavdar.api.domain.Product;
import de.cavdar.api.domain.ProductInput;
import de.cavdar.api.domain.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API – klassisches HTTP/JSON CRUD.
 *
 * Endpunkte:
 *   GET    /api/products        → alle Produkte
 *   GET    /api/products/{id}   → ein Produkt
 *   POST   /api/products        → erstellen
 *   PUT    /api/products/{id}   → aktualisieren
 *   DELETE /api/products/{id}   → löschen
 *
 * Charakteristika:
 *  - Stateless, URL-basierte Ressourcen
 *  - HTTP-Verben definieren die Aktion
 *  - Feste Response-Struktur (Over-/Underfetching möglich)
 *  - HTTP-Statuscodes für Fehlerbehandlung (404, 204, ...)
 */
@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    private final ProductService service;

    public ProductRestController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Product create(@RequestBody ProductInput input) {
        return service.create(input);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody ProductInput input) {
        return service.update(id, input)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
