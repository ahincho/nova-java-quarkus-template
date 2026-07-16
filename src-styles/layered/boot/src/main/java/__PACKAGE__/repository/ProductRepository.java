package __PACKAGE__.repository;

import __PACKAGE__.entity.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Driven port for {@link Product} persistence. The Layered style
 * keeps the repository as a concrete class (no separate interface +
 * impl split — that's a Hexagonal / Clean concern).
 *
 * <p>{@code LayeredArchitectureTest} enforces that this class does
 * not depend on {@code service..} or {@code controller..}.
 */
public final class ProductRepository {

    private final java.util.Map<UUID, Product> store = new java.util.HashMap<>();

    /**
     * Persists a {@link Product} in the in-memory store.
     *
     * @param product the product to save.
     * @return the saved product (same instance, for chaining).
     */
    public Product save(final Product product) {
        store.put(product.getId(), product);
        return product;
    }

    /**
     * Looks up a product by id.
     *
     * @param id the identifier.
     * @return the matching product, or empty if no entry exists.
     */
    public Optional<Product> findById(final UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    /**
     * @return every product currently in the store, in insertion order.
     */
    public List<Product> findAll() {
        return List.copyOf(store.values());
    }
}