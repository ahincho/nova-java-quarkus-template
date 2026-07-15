/**
 * `shared` module — pure Java library that holds value objects,
 * exceptions and domain primitives shared across bounded contexts.
 *
 * <p>This module has NO Quarkus dependencies and NO Nova Platform
 * dependencies; it can be reused by any other JVM module (Spring Boot,
 * Micronaut, plain JUnit). It is the lowest layer of the
 * Nova Platform meta-framework's hexagonal layout.
 */
plugins {
    java
    `java-library`
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.0")
    testImplementation("org.assertj:assertj-core:3.26.3")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}