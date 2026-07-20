package __PACKAGE__.domain.exception;

import __PACKAGE__.domain.model.StudentId;

/**
 * Domain exception: a student who is already enrolled in a section
 * attempted to enroll again.
 *
 * <p>Pure domain type — it carries the offending {@link StudentId} so
 * the infrastructure exception mapper can translate it into the right
 * HTTP response (typically 409 Conflict) without the domain knowing
 * about HTTP.
 */
public final class AlreadyEnrolledException extends RuntimeException {

    private final transient StudentId studentId;

    public AlreadyEnrolledException(final StudentId studentId) {
        super("Student " + studentId.value() + " is already enrolled");
        this.studentId = studentId;
    }

    public StudentId studentId() {
        return studentId;
    }
}