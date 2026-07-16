package __PACKAGE__.shared.domain;

/**
 * Enumeration of stable error codes that bounded contexts surface to
 * the REST layer. The boot module maps each code to an HTTP status
 * (see {@code DomainExceptionMapper}).
 *
 * <p>Catalogue:
 * <ul>
 *   <li>{@link #VALIDATION} — 400 Bad Request. Input failed validation.</li>
 *   <li>{@link #NOT_FOUND} — 404 Not Found. Aggregate does not exist.</li>
 *   <li>{@link #CONFLICT} — 409 Conflict. State transition violates invariants.</li>
 * </ul>
 */
public enum ErrorCode {
    VALIDATION,
    NOT_FOUND,
    CONFLICT
}