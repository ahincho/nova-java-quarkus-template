package __PACKAGE__.boot.infrastructure;

import __PACKAGE__.product.domain.Product;
import __PACKAGE__.product.domain.ProductId;
import __PACKAGE__.product.domain.ProductRepository;
import __PACKAGE__.shared.domain.ConflictException;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of {@link ProductRepository}. Provided as a
 * ready-to-run default for the template; production deployments would
 * replace it with a JDBC- or event-sourced adapter without touching
 * the use cases.
 *
 * <p>Annotated with {@link ApplicationScoped} so Quarkus CDI wires it
 * into the {@link __PACKAGE__.product.application.CreateProductUseCase}
 * on startup.
 */
@ApplicationScoped
public final class InMemoryProductRepository implements ProductRepository {

    private final Map<String, Product> store = new ConcurrentHashMap<>();

    @Override
    public void save(final Product product) {
        final String key = product.id().value();
        if (store.containsKey(key)) {
            throw new ConflictException(
                    "Product with id " + key + " already exists");
        }
        store.put(key, product);
    }

    @Override
    public Optional<Product> findById(final ProductId id) {
        return Optional.ofNullable(store.get(id.value()));
    }
}