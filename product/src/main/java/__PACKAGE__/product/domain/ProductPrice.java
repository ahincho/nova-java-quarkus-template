package __PACKAGE__.product.domain;

import __PACKAGE__.shared.domain.ValidationException;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value object wrapping a product's price. Validates that the price
 * is non-null and strictly greater than zero.
 */
public final class ProductPrice {

    private final BigDecimal value;

    private ProductPrice(final BigDecimal value) {
        this.value = value;
    }

    /**
     * Constructs a {@link ProductPrice} after validation.
     *
     * @param raw the candidate price, must be non-null and &gt; 0.
     * @return a validated {@link ProductPrice}.
     * @throws ValidationException if the candidate is null or non-positive.
     */
    public static ProductPrice of(final BigDecimal raw) {
        if (raw == null) {
            throw new ValidationException("Product price must not be null");
        }
        if (raw.signum() <= 0) {
            throw new ValidationException("Product price must be greater than zero");
        }
        return new ProductPrice(raw);
    }

    public BigDecimal value() {
        return value;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ProductPrice that)) {
            return false;
        }
        return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }
}