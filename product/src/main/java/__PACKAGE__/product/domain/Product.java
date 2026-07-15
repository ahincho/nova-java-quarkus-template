package __PACKAGE__.product.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Aggregate root for a Product. Encapsulates the invariants enforced
 * by {@link ProductName} and {@link ProductPrice}. Created via the
 * static factory {@link #create(ProductId, String, BigDecimal)}.
 */
public final class Product {

    private final ProductId id;
    private final ProductName name;
    private final ProductPrice price;

    private Product(final ProductId id, final ProductName name, final ProductPrice price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    /**
     * Creates a new Product aggregate.
     *
     * @param id    the identifier (already validated by the caller).
     * @param name  the raw product name; validated by {@link ProductName#of}.
     * @param price the raw price; validated by {@link ProductPrice#of}.
     * @return a new aggregate.
     */
    public static Product create(final ProductId id, final String name, final BigDecimal price) {
        Objects.requireNonNull(id, "ProductId must not be null");
        return new Product(id, ProductName.of(name), ProductPrice.of(price));
    }

    public ProductId id() {
        return id;
    }

    public ProductName name() {
        return name;
    }

    public ProductPrice price() {
        return price;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Product that)) {
            return false;
        }
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" + id + ", " + name + ", " + price + "}";
    }
}