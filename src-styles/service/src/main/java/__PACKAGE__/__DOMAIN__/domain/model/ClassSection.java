package __PACKAGE__.__DOMAIN__.domain.model;

import __PACKAGE__.__DOMAIN__.exception.AlreadyEnrolledException;
import __PACKAGE__.__DOMAIN__.exception.SectionFullException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate root: a class section a student can enroll in.
 *
 * <p>Pure domain — no Quarkus, no JPA. It protects its own invariants:
 * a section cannot exceed its {@link Capacity} and a student cannot be
 * enrolled twice. Enrollments are only mutated through this root.
 */
public final class ClassSection {

    private final ClassSectionId id;
    private final Capacity capacity;
    private final List<Enrollment> enrollments;

    public ClassSection(final ClassSectionId id, final Capacity capacity) {
        this.id = id;
        this.capacity = capacity;
        this.enrollments = new ArrayList<>();
    }

    /**
     * Enrolls a student, enforcing capacity and uniqueness.
     *
     * @param studentId the student to enroll.
     * @throws SectionFullException      if the section is at capacity.
     * @throws AlreadyEnrolledException  if already enrolled.
     */
    public void enroll(final StudentId studentId) {
        if (enrollments.size() >= capacity.value()) {
            throw new SectionFullException(id);
        }
        if (isEnrolled(studentId)) {
            throw new AlreadyEnrolledException(studentId);
        }
        enrollments.add(new Enrollment(studentId));
    }

    private boolean isEnrolled(final StudentId studentId) {
        return enrollments.stream()
                .anyMatch(e -> e.studentId().equals(studentId));
    }

    public ClassSectionId id() {
        return id;
    }

    public Capacity capacity() {
        return capacity;
    }

    public List<Enrollment> enrollments() {
        return Collections.unmodifiableList(enrollments);
    }
}