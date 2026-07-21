package __PACKAGE__.application.port.in;

import __PACKAGE__.application.command.EnrollStudentCommand;

/**
 * Inbound port (driving port): the enroll-student use case.
 *
 * <p>This interface is the boundary the inbound adapter (a REST
 * resource) depends on. The controller injects this type, never the
 * concrete implementation — the same inversion the outbound repository
 * port applies, now on the driving side. Implemented by
 * {@code EnrollStudentService} in the application layer.
 */
public interface EnrollStudentUseCase {

    /**
     * Enrolls a student into a class section.
     *
     * @param command the raw input.
     */
    void handle(EnrollStudentCommand command);
}