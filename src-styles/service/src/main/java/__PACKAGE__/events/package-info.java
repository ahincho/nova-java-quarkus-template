/**
 * Cross-cutting eventing infrastructure.
 *
 * <p>Holds the technical plumbing for publishing and consuming
 * integration events across service boundaries: broker/channel
 * configuration, serialization, and the publisher/subscriber adapters.
 * This is transport-level wiring only — domain events themselves live in
 * each feature's {@code domain.event} package; this package is how they
 * leave or enter the service.
 */
package __PACKAGE__.events;