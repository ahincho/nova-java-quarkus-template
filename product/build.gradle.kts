/**
 * `product` module — example bounded context that consumes the
 * {@code shared} module. Demonstrates the canonical hexagonal split:
 * domain → application (use cases) → infrastructure (driven adapters).
 *
 * <p>The module is intentionally framework-agnostic; it has no Quarkus
 * dependency. The boot module wires the application services into the
 * JAX-RS layer.
 */
plugins {
    java
    `java-library`
}

dependencies {
    implementation(project(":shared"))

    testImplementation("org.junit.jupiter:junit-jupiter:6.0.0")
    testImplementation("org.assertj:assertj-core:3.26.3")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}