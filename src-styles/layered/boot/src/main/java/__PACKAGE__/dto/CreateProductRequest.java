package __PACKAGE__.dto;

import java.math.BigDecimal;

/**
 * Request body for {@code POST /api/products}. Decoupled from
 * {@link __PACKAGE__.entity.Product} so persistence concerns never
 * leak into the HTTP transport contract (enforced by
 * {@code LayeredArchitectureTest}).
 */
public record CreateProductRequest(
        String name,
        BigDecimal price) { }