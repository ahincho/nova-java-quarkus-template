package __PACKAGE__.product.domain;

import __PACKAGE__.shared.domain.AggregateId;
import java.util.UUID;

/**
 * Type-safe identifier for a {@link Product} aggregate. Wraps a UUID
 * so equality / hashing are based on the underlying string value.
 */
public final class ProductId extends AggregateId {

    private final String value;

    private ProductId(final String value) {
        this.value = value;
    }

    /**
     * Creates a new {@link ProductId} from a random UUID.
     *
     * @return a fresh identifier, never {@code null}.
     */
    public static ProductId newId() {
        return new ProductId(UUID.randomUUID().toString());
    }

    /**
     * Reconstructs a {@link ProductId} from a previously-serialised value.
     *
     * @param value the UUID string, must not be {@code null} or blank.
     * @return a {@link ProductId} wrapping the supplied value.
     * @throws IllegalArgumentException if the value is null or blank.
     */
    public static ProductId fromString(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ProductId value must not be blank");
        }
        return new ProductId(value);
    }

    @Override
    public String value() {
        return value;
    }
}