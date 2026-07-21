/**
 * Settings for the buildSrc build.
 *
 * <p>buildSrc is an implicitly-included build that Gradle compiles
 * before the main build. It hosts the Nova Platform convention plugins
 * (nova.java-conventions, nova.repositories-conventions,
 * nova.quarkus-conventions) so every generated microservice shares the
 * same toolchain, repositories and Quarkus wiring from a single source
 * of truth.
 */
rootProject.name = "buildSrc"