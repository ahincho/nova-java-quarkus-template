/**
 * nova.java-conventions — base Java configuration for every Nova
 * Platform artifact (service, bff, acl).
 *
 * <p>Single source of truth for the Java toolchain. Applying this
 * plugin gives a module the Java plugin plus the Nova-standard
 * toolchain (Java 25, matching nova-quarkus-parent) and sensible test
 * defaults. Modules that need Quarkus additionally apply
 * `nova.quarkus-conventions`.
 */
plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}