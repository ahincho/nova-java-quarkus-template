/**
 * Cross-cutting authentication and security concerns.
 *
 * <p>Holds the security wiring shared by every inbound adapter: JWT/token
 * validation, identity extraction (resolving the caller into a
 * {@code userId}), request filters and security context propagation. It
 * centralizes how the service establishes <em>who</em> is calling so
 * feature code can assume an already-authenticated identity and never
 * re-implements auth.
 */
package __PACKAGE__.authentication;