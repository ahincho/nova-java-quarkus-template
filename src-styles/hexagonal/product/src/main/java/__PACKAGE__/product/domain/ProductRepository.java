package __PACKAGE__.product.domain;

import java.util.Optional;

/**
 * Driven port (output) for persisting and retrieving Product aggregates.
 * The infrastructure layer (e.g. an in-memory adapter in {@code boot})
 * provides the concrete implementation.
 */
public interface ProductRepository {

    /**
     * Persists a new product. Implementations must reject duplicates by
     * id; the canonical conflict exception is
     * {@link __PACKAGE__.shared.domain.ConflictException}.
     *
     * @param product the aggregate to persist, must not be {@code null}.
     */
    void save(Product product);

    /**
     * Looks up a product by id.
     *
     * @param id the identifier to look up, must not be {@code null}.
     * @return an {@link Optional} containing the product if found.
     */
    Optional<Product> findById(ProductId id);
}