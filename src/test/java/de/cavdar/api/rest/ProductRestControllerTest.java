package de.cavdar.api.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getAllProducts_returnsInitialDemoData() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    void getProductById_returnsProduct() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void getProductById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_returnsCreatedProduct() throws Exception {
        String body = """
                { "name": "Monitor", "description": "4K Display", "price": 499.99 }
                """;
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Monitor"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void updateProduct_returnsUpdatedProduct() throws Exception {
        String body = """
                { "name": "Laptop Pro", "description": "Updated", "price": 1599.99 }
                """;
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop Pro"));
    }

    @Test
    void deleteProduct_returns204() throws Exception {
        mockMvc.perform(delete("/api/products/2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());
    }
}
