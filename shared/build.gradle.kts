/**
 * `shared` module — style-agnostic domain primitives shared by every
 * bounded context in the Nova Platform microservice. The actual Java
 * sources live in {@code src-styles/shared-resources/java/__PACKAGE__/shared/}
 * and are copied here by the {@code rename} task in the root build
 * file.
 *
 * <p>This file only configures the Java toolchain. The package layout
 * and dependencies are intentionally minimal — no Quarkus, no Spring,
 * no third-party libs.
 */
plugins {
    java
}

dependencies {
    // intentionally empty — shared/ has zero framework dependencies by design
}