package __PACKAGE__.service;

import __PACKAGE__.entity.Product;
import __PACKAGE__.repository.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Service layer for the {@code Product} bounded context in the
 * Layered style. Sits between {@code ProductController} and
 * {@code ProductRepository} and owns all business logic (id
 * generation, duplicate detection, price validation).
 *
 * <p>The class is {@code @ApplicationScoped} so a single instance is
 * reused across requests. The {@link ProductRepository} is injected
 * through the constructor (constructor injection is the Nova Platform
 * coding rule — no {@code @Inject} field injection).
 */
@ApplicationScoped
public final class ProductService {

    private final ProductRepository repository;

    /**
     * Constructs the service with its single collaborator.
     *
     * @param repository the product repository (must not be null).
     */
    @Inject
    public ProductService(final ProductRepository repository) {
        this.repository = Objects.requireNonNull(
                repository, "ProductRepository must not be null");
    }

    /**
     * Creates and persists a new product with a freshly minted UUID.
     *
     * @param name  the raw name.
     * @param price the raw price.
     * @return the persisted product.
     * @throws IllegalStateException if a product with the generated
     *                               id already exists.
     */
    public Product create(final String name, final BigDecimal price) {
        final UUID id = UUID.randomUUID();
        final Product candidate = new Product(id, name, price);
        if (repository.findById(id).isPresent()) {
            throw new IllegalStateException(
                    "Product with id " + id + " already exists");
        }
        return repository.save(candidate);
    }

    /**
     * Looks up a product by id.
     *
     * @param id the identifier.
     * @return the matching product.
     * @throws IllegalArgumentException if no product exists with that id.
     */
    public Product findById(final UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product with id " + id + " not found"));
    }

    /**
     * @return every product, in insertion order.
     */
    public List<Product> findAll() {
        return repository.findAll();
    }
}