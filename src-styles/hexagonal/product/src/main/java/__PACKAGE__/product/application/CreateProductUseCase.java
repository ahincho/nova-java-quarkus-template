package __PACKAGE__.product.application;

import __PACKAGE__.product.domain.Product;
import __PACKAGE__.product.domain.ProductId;
import __PACKAGE__.product.domain.ProductRepository;
import __PACKAGE__.shared.domain.ConflictException;
import __PACKAGE__.shared.domain.NotFoundException;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Application service that orchestrates the creation of a Product.
 *
 * <p>This use case is framework-agnostic: it depends only on the
 * {@link ProductRepository} driven port and the domain types from
 * {@code product.domain}. The boot module wires the concrete repository
 * via CDI.
 */
public final class CreateProductUseCase {

    private final ProductRepository repository;

    public CreateProductUseCase(final ProductRepository repository) {
        this.repository = Objects.requireNonNull(repository, "ProductRepository must not be null");
    }

    /**
     * Creates a new product with a freshly-minted {@link ProductId}.
     *
     * @param name  the raw product name (validated by the domain).
     * @param price the raw price (validated by the domain).
     * @return the persisted product.
     * @throws ConflictException if a product with the same id already exists.
     */
    public Product create(final String name, final BigDecimal price) {
        Product candidate = Product.create(ProductId.newId(), name, price);
        repository.findById(candidate.id()).ifPresent(existing -> {
            throw new ConflictException(
                    "Product with id " + candidate.id().value() + " already exists");
        });
        repository.save(candidate);
        return candidate;
    }

    /**
     * Looks up a product by id.
     *
     * @param id the identifier.
     * @return the product.
     * @throws NotFoundException if no product exists with that id.
     */
    public Product findById(final ProductId id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Product with id " + id.value() + " not found"));
    }
}