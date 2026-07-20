package __PACKAGE__.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object: identity of a {@link ClassSection}.
 *
 * <p>Immutable, compared by value. Wraps a {@link UUID} so the domain
 * never leaks a raw primitive as an identity.
 */
public record ClassSectionId(UUID value) {

    public ClassSectionId {
        Objects.requireNonNull(value, "ClassSectionId value must not be null");
    }

    /**
     * @return a new random identity.
     */
    public static ClassSectionId newId() {
        return new ClassSectionId(UUID.randomUUID());
    }

    /**
     * @param raw the string form of a UUID.
     * @return the identity parsed from {@code raw}.
     */
    public static ClassSectionId of(final String raw) {
        return new ClassSectionId(UUID.fromString(raw));
    }
}