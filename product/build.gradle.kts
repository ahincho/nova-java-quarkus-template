/**
 * `product` module — Hexagonal / Clean only. Exists as a stub so
 * Gradle's settings.gradle.kts can include it at config time. The
 * actual Java sources live in
 * {@code src-styles/hexagonal/product/src/main/java/__PACKAGE__/product/}
 * and {@code src-styles/clean/product/src/main/java/__PACKAGE__/product/}
 * respectively, and are copied here by the {@code rename} task in the
 * root build file.
 *
 * <p>For the Layered style this module is intentionally empty: all
 * code lives inside {@code boot/}.
 */
plugins {
    java
}

dependencies {
    implementation(project(":shared"))
}