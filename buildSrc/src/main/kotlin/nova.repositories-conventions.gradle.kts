/**
 * nova.repositories-conventions — declares the repositories every Nova
 * Platform artifact resolves dependencies from.
 *
 * <p>Central source of truth for the two GitHub Packages registries
 * that host the Nova JARs (notifications extension + architecture
 * rules). Neither is mirrored on Maven Central, so without these a
 * build fails to resolve `pe.edu.nova.java*` artifacts.
 *
 * <p>Credentials come from the NOVA_RELEASE_PAT environment variable
 * (falling back to GITHUB_TOKEN for CI). When absent, the repositories
 * are still declared but unauthenticated — useful for offline tasks
 * that don't touch Nova artifacts.
 */

val novaReleasePat: String? = System.getenv("NOVA_RELEASE_PAT")
val githubToken: String? = System.getenv("GITHUB_TOKEN")
val novaToken: String? = novaReleasePat ?: githubToken

repositories {
    mavenLocal()
    mavenCentral()

    listOf(
        "nova-java-notifications-quarkus-extension",
        "nova-java-architecture-rules"
    ).forEach { repo ->
        maven {
            name = "GitHubPackages-${repo.split("-").joinToString("") { it.replaceFirstChar(Char::uppercase) }}"
            url = uri("https://maven.pkg.github.com/ahincho/$repo")
            content {
                includeGroupByRegex("pe\\.edu\\.nova\\.java.*")
            }
            if (!novaToken.isNullOrBlank()) {
                credentials {
                    username = System.getenv("GITHUB_ACTOR") ?: "x-access-token"
                    password = novaToken
                }
            }
        }
    }
}