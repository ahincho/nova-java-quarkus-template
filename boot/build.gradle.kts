/**
 * `boot` module — Quarkus entry point that wires the bounded contexts
 * (product, future ones) into a runnable microservice. It is the only
 * module that depends on Quarkus + the Nova Platform notifications
 * extension + (when applicable) the Nova Platform architecture rules.
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
 *
 * <p>The {@code nova-architecture-rules} test dependency is only added
 * when the chosen style is {@code layered} or {@code clean}; for
 * {@code hexagonal} the architectural rules are still encoded in the
 * skeleton (domain does not depend on adapters, etc.) and an extra
 * ArchUnit test will land in phase 2.
 */
plugins {
    java
    id("io.quarkus")
}

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
val novaArchitectureRulesVersion: String =
    project.findProperty("novaArchitectureRulesVersion") as String?
        ?: error("novaArchitectureRulesVersion is not defined in gradle.properties")
val styleProp: String =
    (project.findProperty("style") as String?) ?: "layered"

dependencies {
    implementation(project(":shared"))

    // Product module only exists in hexagonal/clean variants. The
    // Layered variant keeps everything inside boot/ — see
    // src-styles/layered/.
    if (project.file("../product/build.gradle.kts").exists()) {
        implementation(project(":product"))
    }

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

    // Architectural compliance via nova-architecture-rules. Always
    // present at test scope — the LayeredArchitectureTest is activated
    // by the ArchitectureTest class shipped in layered/clean styles.
    // For hexagonal the class is currently a no-op stub until phase 2.
    testImplementation("pe.edu.nova.java.libs:nova-architecture-rules:$novaArchitectureRulesVersion")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}