package __PACKAGE__.shared.domain;

/**
 * Thrown when a use case receives invalid input. Maps to HTTP 400 via
 * the boot module's exception mapper.
 */
public class ValidationException extends DomainException {

    public ValidationException(final String message) {
        super(ErrorCode.VALIDATION, message);
    }
}