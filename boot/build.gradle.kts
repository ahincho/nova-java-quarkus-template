/**
 * `boot` module — Quarkus entry point that wires the bounded contexts
 * (product, future ones) into a runnable microservice. It is the only
 * module that depends on Quarkus + the Nova Platform notifications
 * extension.
 *
 * <p>The module applies the {@code io.quarkus} plugin which:
 * <ul>
 *   <li>compiles main and test sources with the project's Java toolchain,</li>
 *   <li>provides {@code quarkusDev}, {@code quarkusBuild}, and
 *       {@code quarkusTest} tasks,</li>
 *   <li>generates the Quarkus build-time index from each module's classes.</li>
 * </ul>
 *
 * <p>Application configuration lives in {@code src/main/resources/application.properties}
 * and follows the Nova Platform convention: every notification channel
 * has a kebab-case property under the {@code nova.notifications.*} prefix.
 */
plugins {
    java
    id("io.quarkus")
}

// Gradle properties from the root gradle.properties are propagated to
// every subproject's Project instance. Use findProperty(...) for safe,
// nullable access; required values are non-null at runtime because the
// root build file is the single source of truth for these coordinates.
val quarkusPlatformGroupId: String =
    project.findProperty("quarkusPlatformGroupId") as String?
        ?: error("quarkusPlatformGroupId is not defined in gradle.properties")
val quarkusPlatformArtifactId: String =
    project.findProperty("quarkusPlatformArtifactId") as String?
        ?: error("quarkusPlatformArtifactId is not defined in gradle.properties")
val quarkusPlatformVersion: String =
    project.findProperty("quarkusPlatformVersion") as String?
        ?: error("quarkusPlatformVersion is not defined in gradle.properties")
val novaNotificationsQuarkusExtensionVersion: String =
    project.findProperty("novaNotificationsQuarkusExtensionVersion") as String?
        ?: error("novaNotificationsQuarkusExtensionVersion is not defined in gradle.properties")

dependencies {
    implementation(project(":shared"))
    implementation(project(":product"))

    // Quarkus core extensions (REST + health + JSON).
    implementation(enforcedPlatform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-smallrye-health")
    implementation("io.quarkus:quarkus-arc")

    // Nova Platform notifications adapter (auto-wires NotificationFacade).
    implementation("pe.edu.nova.java.starters:nova-notifications-quarkus-extension:$novaNotificationsQuarkusExtensionVersion")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured:5.5.0")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}