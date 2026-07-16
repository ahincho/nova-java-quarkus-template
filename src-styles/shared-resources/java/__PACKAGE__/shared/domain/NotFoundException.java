package __PACKAGE__.shared.domain;

/**
 * Thrown when an aggregate lookup yields no result. Maps to HTTP 404
 * via the boot module's exception mapper.
 */
public class NotFoundException extends DomainException {

    public NotFoundException(final String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}