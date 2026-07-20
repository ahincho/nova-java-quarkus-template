package __PACKAGE__.application.command;

/**
 * Input command for the enroll-student use case.
 *
 * <p>A plain, framework-free carrier of the raw input the use case
 * needs. It uses primitive/string types (not domain value objects) so
 * the inbound adapter — a REST resource — can build it without reaching
 * into the domain; the use case is responsible for turning these into
 * value objects.
 */
public record EnrollStudentCommand(
        String sectionId,
        String studentId) {
}