/**
 * nova.quarkus-conventions — Quarkus runtime configuration for Nova
 * Platform artifacts that boot a Quarkus application (service, bff, acl).
 *
 * <p>Applies on top of `nova.java-conventions` (toolchain + test
 * defaults) and `nova.repositories-conventions` (GitHub Packages). It
 * wires:
 * <ul>
 *   <li>the io.quarkus Gradle plugin,</li>
 *   <li>the Quarkus platform BOM (enforced),</li>
 *   <li>the core extensions every Nova artifact needs (REST + JSON +
 *       health + CDI),</li>
 *   <li>the Nova notifications extension,</li>
 *   <li>test dependencies (JUnit5, REST Assured) and the Nova
 *       architecture rules for ArchUnit enforcement.</li>
 * </ul>
 *
 * <p>All versions are read from gradle.properties so there is a single
 * source of truth shared with the Maven path (nova-quarkus-parent).
 */
plugins {
    id("nova.java-conventions")
    id("nova.repositories-conventions")
    id("io.quarkus")
}

fun requiredProperty(name: String): String =
    (project.findProperty(name) as String?)
        ?: error("$name is not defined in gradle.properties")

val quarkusPlatformGroupId = requiredProperty("quarkusPlatformGroupId")
val quarkusPlatformArtifactId = requiredProperty("quarkusPlatformArtifactId")
val quarkusPlatformVersion = requiredProperty("quarkusPlatformVersion")
val novaNotificationsQuarkusExtensionVersion =
    requiredProperty("novaNotificationsQuarkusExtensionVersion")
val novaArchitectureRulesVersion =
    requiredProperty("novaArchitectureRulesVersion")

dependencies {
    // Quarkus platform BOM — aligns every extension to the same version.
    "implementation"(enforcedPlatform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))

    // Core Quarkus extensions shared by every Nova artifact.
    "implementation"("io.quarkus:quarkus-rest")
    "implementation"("io.quarkus:quarkus-rest-jackson")
    "implementation"("io.quarkus:quarkus-smallrye-health")
    "implementation"("io.quarkus:quarkus-arc")

    // Nova Platform notifications adapter (auto-wires NotificationFacade).
    "implementation"("pe.edu.nova.java.starters:nova-notifications-quarkus-extension:$novaNotificationsQuarkusExtensionVersion")

    // Test stack.
    "testImplementation"("io.quarkus:quarkus-junit5")
    "testImplementation"("io.rest-assured:rest-assured:5.5.0")

    // Architectural compliance (ArchUnit rules shipped by Nova).
    "testImplementation"("pe.edu.nova.java.libs:nova-architecture-rules:$novaArchitectureRulesVersion")
}