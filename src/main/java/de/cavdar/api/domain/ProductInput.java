package de.cavdar.api.domain;

/**
 * Eingabe-DTO für Create/Update – wird von REST (@RequestBody),
 * GraphQL (@Argument) und gRPC (aus Proto-Request gebaut) verwendet.
 */
public record ProductInput(String name, String description, Double price) {}
