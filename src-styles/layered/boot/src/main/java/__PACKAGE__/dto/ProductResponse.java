package __PACKAGE__.dto;

import __PACKAGE__.entity.Product;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response body for {@code GET /api/products/\{id\}} and
 * {@code POST /api/products}. Decoupled from {@link Product} on
 * purpose — see {@code CreateProductRequest} for the rationale.
 */
public record ProductResponse(
        UUID id,
        String name,
        BigDecimal price) {

    /**
     * Maps a domain {@link Product} to its public response shape.
     *
     * @param product the source entity.
     * @return the response DTO.
     */
    public static ProductResponse from(final Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice());
    }
}