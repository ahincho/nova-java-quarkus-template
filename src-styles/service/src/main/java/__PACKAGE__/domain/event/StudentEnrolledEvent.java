package __PACKAGE__.domain.event;

import __PACKAGE__.domain.model.ClassSectionId;
import __PACKAGE__.domain.model.StudentId;
import java.time.Instant;

/**
 * Domain event: a student was enrolled in a class section.
 *
 * <p>Named in the past tense — it records a fact that already happened.
 * Pure domain type: the {@code infrastructure/messaging} adapter is what
 * decides how (and whether) to publish it to the outside world.
 */
public record StudentEnrolledEvent(
        ClassSectionId sectionId,
        StudentId studentId,
        Instant occurredAt) {

    public static StudentEnrolledEvent of(
            final ClassSectionId sectionId,
            final StudentId studentId) {
        return new StudentEnrolledEvent(sectionId, studentId, Instant.now());
    }
}