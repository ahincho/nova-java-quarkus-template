package __PACKAGE__.entity;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a {@code Product}. In the Layered style
 * entities are simple POJOs / records — they live at the bottom of
 * the dependency graph and are imported by services, repositories and
 * (indirectly) controllers via the {@code dto..} mapping layer.
 *
 * <p>No JAX-RS, JPA or Spring/CDI annotations live here on purpose:
 * the {@code LayeredArchitectureTest} enforces that {@code entity..}
 * does not depend on any other layer package.
 */
public final class Product {

    private final UUID id;
    private final String name;
    private final java.math.BigDecimal price;

    /**
     * Constructs a new Product.
     *
     * @param id    the unique identifier (UUID).
     * @param name  the product name (must be non-blank).
     * @param price the unit price (must be non-negative).
     */
    public Product(
            final UUID id,
            final String name,
            final java.math.BigDecimal price) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = validateName(name);
        this.price = validatePrice(price);
    }

    private static String validateName(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        return value;
    }

    private static java.math.BigDecimal validatePrice(
            final java.math.BigDecimal value) {
        if (value == null || value.signum() < 0) {
            throw new IllegalArgumentException(
                    "price must be non-negative");
        }
        return value;
    }

    /** @return the product id. */
    public UUID getId() { return id; }

    /** @return the product name. */
    public String getName() { return name; }

    /** @return the unit price. */
    public java.math.BigDecimal getPrice() { return price; }
}