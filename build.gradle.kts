import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Properties

/**
 * Root build file for a Nova Platform microservice instance generated
 * from the {@code nova-java-quarkus-template} repository.
 *
 * <p>This file wires:
 * <ul>
 *   <li>the Quarkus platform BOM (same version as {@code nova-quarkus-parent}
 *       in the Maven path: Quarkus 3.33.2.1 LTS),</li>
 *   <li>the {@code io.quarkus} Gradle plugin (applies the extension
 *       convention to every module that declares it),</li>
 *   <li>the {@code nova-notifications-quarkus-extension} GitHub Packages
 *       repository (NOVA_RELEASE_PAT) so each subproject can resolve
 *       the adapter JAR published by Nova Platform.</li>
 * </ul>
 *
 * <p>After instantiating the template, run:
 *
 * <pre>
 * ./gradlew rename -PgroupId=com.acme -PartifactId=my-service -Ppackage=com.acme.my
 * </pre>
 *
 * <p>The rename task rewrites every tracked text file in place
 * (replacing {@code __GROUP__}, {@code __ARTIFACT__}, {@code __PACKAGE__})
 * and updates this file's group/version in {@code gradle.properties}.
 */
plugins {
    java
    id("io.quarkus") version "3.33.2.1" apply false
}

val novaReleasePat: String? = System.getenv("NOVA_RELEASE_PAT")
val githubToken: String? = System.getenv("GITHUB_TOKEN")

allprojects {
    group = providers.gradleProperty("group").get()
    version = providers.gradleProperty("version").get()

    repositories {
        mavenLocal()
        mavenCentral()
        // GitHub Packages of nova-notifications-quarkus-extension. Without
        // this entry, ./gradlew build fails because Quarkus extension classes
        // (e.g. NotificationsConfig) are not mirrored on Maven Central — only
        // on the Nova Platform GitHub Packages registry.
        maven {
            name = "GitHubPackages-NovaQuarkusExtension"
            url = uri("https://maven.pkg.github.com/ahincho/nova-java-notifications-quarkus-extension")
            content {
                includeGroupByRegex("pe\\.edu\\.nova\\.java.*")
            }
            val token = novaReleasePat ?: githubToken
            if (!token.isNullOrBlank()) {
                credentials {
                    username = System.getenv("GITHUB_ACTOR") ?: "x-access-token"
                    password = token
                }
            }
        }
    }
}

subprojects {
    plugins.withType<JavaPlugin>().configureEach {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(25))
            }
        }
    }
}

/**
 * `rename` task — the Nova Platform Gradle-template equivalent of the
 * Maven archetype's `archetype:generate` parameter substitution.
 *
 * <p>Replaces four placeholders in every tracked text file:
 * <ul>
 *   <li>{@code __GROUP__} → {@code -PgroupId}</li>
 *   <li>{@code __ARTIFACT__} → {@code -PartifactId}</li>
 *   <li>{@code __PACKAGE__} → {@code -Ppackage} (dots preserved for
 *       Java {@code package} and {@code import} declarations)</li>
 *   <li>{@code __PACKAGE_PATH__} → {@code -Ppackage} with dots replaced
 *       with slashes (for folder paths inside source roots)</li>
 * </ul>
 *
 * <p>Only files under the project root are processed. The {@code build/},
 * {@code .gradle/}, {@code gradle/wrapper/} directories and the
 * {@code gradlew} / {@code gradlew.bat} scripts are excluded to avoid
 * clobbering the local Gradle daemon's transient state.
 *
 * <p>The task rewrites {@code gradle.properties} with the new
 * {@code group}, {@code artifactId}, and {@code version} values so that
 * subsequent Gradle invocations use the renamed coordinates.
 *
 * <p>Run once after cloning the template:
 *
 * <pre>
 * ./gradlew rename -PgroupId=com.acme -PartifactId=my-service -Ppackage=com.acme.my
 * </pre>
 *
 * <p>Note: the rename does NOT move source files between folders. The
 * template uses fixed module names ({@code shared/}, {@code product/},
 * {@code boot/}) — only the Java {@code package} declarations inside
 * those files change. If you want to relocate sources, do it manually
 * with your IDE after the rename.
 */
tasks.register("rename") {
    val groupProp: String = (project.findProperty("groupId") as String?)
        ?: error("Missing -PgroupId=<your.group>")
    val artifactProp: String = (project.findProperty("artifactId") as String?)
        ?: error("Missing -PartifactId=<your-artifact>")
    val packageProp: String = (project.findProperty("package") as String?)
        ?: error("Missing -Ppackage=<your.java.package>")

    val packagePath = packageProp.replace('.', '/')

    val textFilePatterns = listOf(
        "**/*.kt",
        "**/*.kts",
        "**/*.java",
        "**/*.properties",
        "**/*.yaml",
        "**/*.yml",
        "**/*.xml",
        "**/*.md",
        "**/*.gitignore",
        "**/.gitignore",
        "**/Dockerfile*"
    )

    val excludePatterns = listOf(
        "build/**",
        ".gradle/**",
        "gradle/wrapper/**",
        "gradlew",
        "gradlew.bat"
    )

    doLast {
        val rootDir: Path = rootProject.projectDir.toPath()
        val filesTouched = mutableSetOf<Path>()

        rootDir.toFile().walkTopDown()
            .filter { it.isFile }
            .filter { f ->
                val rel = rootDir.relativize(f.toPath()).toString().replace('\\', '/')
                val matchesInclude = textFilePatterns.any { glob ->
                    FileSystems.getDefault()
                        .getPathMatcher("glob:$glob")
                        .matches(Paths.get(rel))
                }
                val matchesExclude = excludePatterns.any { ex ->
                    FileSystems.getDefault()
                        .getPathMatcher("glob:$ex")
                        .matches(Paths.get(rel))
                }
                matchesInclude && !matchesExclude
            }
            .forEach { f ->
                val original = f.readText()
                val rewritten = original
                    .replace("__GROUP__", groupProp)
                    .replace("__ARTIFACT__", artifactProp)
                    .replace("__PACKAGE__", packageProp)
                    .replace("__PACKAGE_PATH__", packagePath)
                if (rewritten != original) {
                    f.writeText(rewritten)
                    filesTouched.add(f.toPath())
                }
            }

        // Update gradle.properties with the new group / artifactId / version
        // while preserving any other keys the file already carries (Quarkus
        // platform coords, Nova extension version, toolchain flags, etc.).
        val propsFile = rootDir.resolve("gradle.properties").toFile()
        val props = Properties().apply {
            propsFile.inputStream().use { stream -> load(stream) }
        }
        props.setProperty("group", groupProp)
        props.setProperty("artifactId", artifactProp)
        props.setProperty("version", "0.1.0-SNAPSHOT")
        propsFile.outputStream().bufferedWriter().use { writer ->
            writer.write("# Updated by nova-java-quarkus-template rename task\n")
            for ((key, value) in props.entries.sortedBy { (it.key as String) }) {
                writer.write("$key=$value\n")
            }
        }

        println()
        println("== Nova Platform template rename ==")
        println("   groupId   = $groupProp")
        println("   artifactId= $artifactProp")
        println("   package   = $packageProp ($packagePath)")
        println("   files touched: ${filesTouched.size}")
        println("   gradle.properties updated with new coordinates.")
        println()
        println("Next steps:")
        println("  1. git add -A && git commit -m \"rename: $artifactProp\"")
        println("  2. ./gradlew build")
        println("  3. ./gradlew :boot:quarkusDev")
    }
}