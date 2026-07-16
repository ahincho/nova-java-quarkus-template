package __PACKAGE__.shared.domain;

/**
 * Base class for domain-level exceptions. Subclasses declare a stable
 * {@link ErrorCode} that the REST layer maps to HTTP status codes
 * (see {@code boot}'s {@code DomainExceptionMapper}).
 */
public class DomainException extends RuntimeException {

    private final ErrorCode code;

    public DomainException(final ErrorCode code, final String message) {
        super(message);
        this.code = code;
    }

    public final ErrorCode code() {
        return code;
    }
}