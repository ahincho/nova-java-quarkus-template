package __PACKAGE__.infrastructure.web.in.mapper;

import __PACKAGE__.application.command.EnrollStudentCommand;
import __PACKAGE__.infrastructure.web.in.request.EnrollStudentRequest;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Maps the inbound HTTP request into the application command.
 *
 * <p>Lives in the driving adapter ({@code web/in/mapper}) so the
 * controller stays free of any request-to-command translation and the
 * command (the inbound port's contract) never carries HTTP concerns.
 * A CDI bean so the controller injects it.
 */
@ApplicationScoped
public class EnrollStudentRequestMapper {

    /**
     * @param sectionId the section id taken from the path.
     * @param request   the HTTP request body.
     * @return the application command for the use case.
     */
    public EnrollStudentCommand toCommand(
            final String sectionId,
            final EnrollStudentRequest request) {
        return new EnrollStudentCommand(sectionId, request.studentId());
    }
}