/**
 * buildSrc module — hosts the Nova Platform convention plugins.
 *
 * <p>Applying `kotlin-dsl` lets us author precompiled script plugins
 * (the `*.gradle.kts` files under src/main/kotlin). Each generated
 * microservice then applies them by id (e.g. `nova.quarkus-conventions`)
 * instead of duplicating repository, toolchain and Quarkus wiring.
 *
 * <p>Any third-party plugin that a convention plugin applies by id
 * (here: io.quarkus) must be on buildSrc's own classpath as an
 * implementation dependency — that's what the Quarkus plugin marker
 * dependency below provides. The version is the single source of truth
 * for the Quarkus Gradle plugin used across every generated artifact.
 */
plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    // Makes the io.quarkus plugin resolvable from the convention plugins
    // (nova.quarkus-conventions applies it by id). The coordinate is the
    // Quarkus Gradle plugin marker; keep the version aligned with
    // quarkusPlatformVersion in gradle.properties (3.33.2.1 LTS).
    implementation("io.quarkus:io.quarkus.gradle.plugin:3.33.2.1")
}