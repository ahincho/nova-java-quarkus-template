/**
 * Settings for a Nova Platform microservice generated from the
 * nova-java-quarkus-template.
 *
 * <p>Single-module layout: the generated service IS the root project.
 * There is no boot/ / product/ / shared/ split — the archetype ships a
 * flat, self-contained module whose name is the artifactId the developer
 * passes to the {@code rename} task (-PartifactId). Until rename runs,
 * the placeholder {@code __ARTIFACT__} is used.
 */
rootProject.name = providers.gradleProperty("artifactId").orElse("__ARTIFACT__").get()