/**
 * Cross-cutting configuration for the service.
 *
 * <p>Holds application-wide configuration beans and mappings that do not
 * belong to any single feature: {@code @ConfigMapping} interfaces,
 * CDI producers for shared infrastructure, and startup/observer wiring.
 * Keep feature-specific configuration inside the feature's own packages;
 * only truly global configuration lives here.
 */
package __PACKAGE__.config;