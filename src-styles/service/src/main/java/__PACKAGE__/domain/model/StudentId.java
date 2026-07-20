package __PACKAGE__.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object: identity of a student.
 *
 * <p>Immutable, compared by value. It is an identity wrapper, not an
 * entity — it has no lifecycle of its own, so it lives among the value
 * objects.
 */
public record StudentId(UUID value) {

    public StudentId {
        Objects.requireNonNull(value, "StudentId value must not be null");
    }

    /**
     * @param raw the string form of a UUID.
     * @return the identity parsed from {@code raw}.
     */
    public static StudentId of(final String raw) {
        return new StudentId(UUID.fromString(raw));
    }
}