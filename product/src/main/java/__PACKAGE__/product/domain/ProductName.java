package __PACKAGE__.product.domain;

import __PACKAGE__.shared.domain.ValidationException;
import java.util.Objects;

/**
 * Value object wrapping a product's display name. Validates that the
 * name is non-blank and at most 120 characters.
 */
public final class ProductName {

    private static final int MAX_LENGTH = 120;

    private final String value;

    private ProductName(final String value) {
        this.value = value;
    }

    /**
     * Constructs a {@link ProductName} after validation.
     *
     * @param raw the candidate name, must be non-blank and at most
     *            {@value #MAX_LENGTH} characters.
     * @return a validated {@link ProductName}.
     * @throws ValidationException if the candidate is blank or too long.
     */
    public static ProductName of(final String raw) {
        if (raw == null || raw.isBlank()) {
            throw new ValidationException("Product name must not be blank");
        }
        if (raw.length() > MAX_LENGTH) {
            throw new ValidationException(
                    "Product name must be at most " + MAX_LENGTH + " characters");
        }
        return new ProductName(raw);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ProductName that)) {
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
        return value;
    }
}