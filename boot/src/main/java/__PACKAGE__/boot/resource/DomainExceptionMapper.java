package __PACKAGE__.boot.resource;

import __PACKAGE__.shared.domain.DomainException;
import __PACKAGE__.shared.domain.ErrorCode;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

/**
 * Maps domain exceptions to HTTP responses. The mapping table is the
 * single source of truth for translating {@link ErrorCode} to status
 * codes, so the use cases stay framework-free.
 */
@Provider
public final class DomainExceptionMapper implements ExceptionMapper<DomainException> {

    @Override
    public Response toResponse(final DomainException exception) {
        final int status = switch (exception.code()) {
            case VALIDATION -> 400;
            case NOT_FOUND -> 404;
            case CONFLICT -> 409;
        };
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of(
                        "code", exception.code().name(),
                        "message", exception.getMessage()))
                .build();
    }
}