package __PACKAGE__.shared.domain;

/**
 * Thrown when a state transition would violate an aggregate invariant.
 * Maps to HTTP 409 via the boot module's exception mapper.
 */
public class ConflictException extends DomainException {

    public ConflictException(final String message) {
        super(ErrorCode.CONFLICT, message);
    }
}