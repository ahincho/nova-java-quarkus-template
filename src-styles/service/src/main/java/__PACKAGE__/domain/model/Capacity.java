package __PACKAGE__.domain.model;

/**
 * Value object: the maximum number of enrollments a
 * {@link ClassSection} allows.
 *
 * <p>Immutable and self-validating: a capacity below 1 cannot exist,
 * so the invariant is enforced at construction time and the rest of
 * the domain never needs to re-check it.
 */
public record Capacity(int value) {

    public Capacity {
        if (value < 1) {
            throw new IllegalArgumentException(
                    "Capacity must be at least 1, was " + value);
        }
    }

    public static Capacity of(final int value) {
        return new Capacity(value);
    }
}