package __PACKAGE__.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Entity internal to the {@link ClassSection} aggregate: a single
 * student's enrollment.
 *
 * <p>It has its own identity ({@link StudentId} + the moment it was
 * created) and a lifecycle, which is what makes it an entity rather
 * than a value object. It is never accessed from outside the aggregate
 * root — {@link ClassSection} owns and mutates it.
 */
public final class Enrollment {

    private final StudentId studentId;
    private final Instant enrolledAt;

    Enrollment(final StudentId studentId) {
        this.studentId = Objects.requireNonNull(studentId, "studentId must not be null");
        this.enrolledAt = Instant.now();
    }

    public StudentId studentId() {
        return studentId;
    }

    public Instant enrolledAt() {
        return enrolledAt;
    }
}