package __PACKAGE__.shared.domain;

import java.util.Objects;

/**
 * Marker base class for aggregate identifiers in the Nova Platform
 * shared module. Subclasses add type-safe value semantics by overriding
 * {@link #value()} (and typically delegating to a UUID, Long, or String).
 *
 * <p>Centralising the marker here lets the {@code product} and any
 * future bounded-context modules share an identifier contract without
 * pulling in framework dependencies.
 */
public abstract class AggregateId {

    /**
     * The wrapped identifier value. Implementations must return the
     * underlying primitive in its canonical form (e.g. UUID.toString()).
     *
     * @return the identifier value, never {@code null}.
     */
    public abstract String value();

    @Override
    public final boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AggregateId that)) {
            return false;
        }
        return Objects.equals(this.value(), that.value());
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(value());
    }

    @Override
    public final String toString() {
        return getClass().getSimpleName() + "(" + value() + ")";
    }
}