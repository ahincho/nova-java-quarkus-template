package __PACKAGE__.__DOMAIN__.adapter.in.web.mapper;

import __PACKAGE__.__DOMAIN__.exception.AlreadyEnrolledException;
import __PACKAGE__.__DOMAIN__.exception.SectionFullException;
import __PACKAGE__.__DOMAIN__.exception.SectionNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Translates domain exceptions into HTTP responses so the domain never
 * needs to know about HTTP status codes.
 *
 * <p>Lives in {@code adapter/in/web/mapper} — outbound
 * translation from the domain's language to the transport's. Unmapped
 * runtime exceptions are rethrown so the default handler produces a 500.
 */
@Provider
public class DomainExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(final RuntimeException exception) {
        if (exception instanceof SectionNotFoundException) {
            return build(Response.Status.NOT_FOUND, exception.getMessage());
        }
        if (exception instanceof SectionFullException
                || exception instanceof AlreadyEnrolledException) {
            return build(Response.Status.CONFLICT, exception.getMessage());
        }
        throw exception;
    }

    private Response build(final Response.Status status, final String message) {
        return Response.status(status)
                .entity(new ErrorResponse(status.getStatusCode(), message))
                .build();
    }

    /** Minimal error body returned to the client. */
    public record ErrorResponse(int status, String message) {
    }
}