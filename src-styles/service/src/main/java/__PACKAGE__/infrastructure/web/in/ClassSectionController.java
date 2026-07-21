package __PACKAGE__.infrastructure.web.in;

import __PACKAGE__.application.port.in.EnrollStudentUseCase;
import __PACKAGE__.infrastructure.web.in.mapper.EnrollStudentRequestMapper;
import __PACKAGE__.infrastructure.web.in.request.EnrollStudentRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Inbound web adapter for class-section operations.
 *
 * <p>Lives in {@code infrastructure/web/in} — a driving adapter: it
 * receives HTTP and drives the application through an inbound port.
 * Thin by design: it maps HTTP to the application boundary and back,
 * holding no business logic and no request-to-command translation —
 * that is delegated to {@link EnrollStudentRequestMapper}. It depends
 * on the {@link EnrollStudentUseCase} port interface, never on the
 * concrete service.
 */
@Path("/sections")
public class ClassSectionController {

    private final EnrollStudentUseCase enrollStudentUseCase;
    private final EnrollStudentRequestMapper enrollStudentRequestMapper;

    public ClassSectionController(
            final EnrollStudentUseCase enrollStudentUseCase,
            final EnrollStudentRequestMapper enrollStudentRequestMapper) {
        this.enrollStudentUseCase = enrollStudentUseCase;
        this.enrollStudentRequestMapper = enrollStudentRequestMapper;
    }

    /**
     * Enrolls a student into a section.
     *
     * @param sectionId the target section.
     * @param request   the request body carrying the student id.
     * @return 204 No Content on success.
     */
    @POST
    @Path("/{sectionId}/enrollments")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response enroll(
            @PathParam("sectionId") final String sectionId,
            final EnrollStudentRequest request) {
        enrollStudentUseCase.handle(
                enrollStudentRequestMapper.toCommand(sectionId, request));
        return Response.noContent().build();
    }
}