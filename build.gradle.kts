import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.Properties

/**
 * Root build file for a Nova Platform microservice instance generated
 * from the {@code nova-java-quarkus-template} repository.
 *
 * <p>Wires:
 * <ul>
 *   <li>the Quarkus platform BOM (3.33.2.1 LTS, same as
 *       {@code nova-quarkus-parent} in the Maven path),</li>
 *   <li>the {@code io.quarkus} Gradle plugin (applied per module),</li>
 *   <li>the {@code nova-notifications-quarkus-extension} and
 *       {@code nova-architecture-rules} GitHub Packages repositories
 *       (NOVA_RELEASE_PAT) so each subproject can resolve the
 *       Nova Platform JARs.</li>
 * </ul>
 *
 * <p>After instantiating the template, run:
 *
 * <pre>
 * ./gradlew rename \
 *     -PgroupId=com.acme \
 *     -PartifactId=my-service \
 *     -Ppackage=com.acme.my \
 *     -Pstyle=layered
 * </pre>
 *
 * <p>The {@code -Pstyle=} flag selects the architectural style
 * ({@code layered}, {@code clean}, or {@code hexagonal}). The task
 * copies the matching skeleton from {@code src-styles/<style>/} into
 * the active modules, rewrites the placeholders
 * ({@code __GROUP__}, {@code __ARTIFACT__}, {@code __PACKAGE__},
 * {@code __PACKAGE_PATH__}, {@code __STYLE__}) and updates
 * {@code gradle.properties}.
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
        // GitHub Packages of nova-notifications-quarkus-extension and
        // nova-architecture-rules. Without these entries, ./gradlew
        // build fails because neither artifact is mirrored on Maven
        // Central — they only live on the Nova Platform GitHub
        // Packages registry.
        maven {
            name = "GitHubPackages-NovaPlatform"
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
        maven {
            name = "GitHubPackages-NovaArchitectureRules"
            url = uri("https://maven.pkg.github.com/ahincho/nova-java-architecture-rules")
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
 * Architectural styles supported by the template. Each value maps to
 * a folder under {@code src-styles/} containing the skeleton that the
 * {@code rename} task will copy into the active modules.
 */
enum class NovaStyle(val folder: String, val description: String) {
    LAYERED("layered", "Traditional N-tier (controller/service/repository/entity/dto)"),
    CLEAN("clean", "Uncle Bob Clean Architecture (4 concentric rings)"),
    HEXAGONAL("hexagonal", "Ports & Adapters / Hexagonal (domain + application + adapters)");

    companion object {
        fun fromIdentifier(id: String): NovaStyle =
                values().firstOrNull { it.folder == id.lowercase() }
                        ?: error("Unknown style '$id'. "
                                + "Valid values: ${values().joinToString { it.folder }}")
    }
}

/**
 * `rename` task — the Nova Platform Gradle-template equivalent of the
 * Maven archetype's `archetype:generate` parameter substitution.
 *
 * <p>Accepts four parameters:
 * <ul>
 *   <li>{@code -PgroupId=<your.group>}</li>
 *   <li>{@code -PartifactId=<your-artifact>}</li>
 *   <li>{@code -Ppackage=<your.java.package>}</li>
 *   <li>{@code -Pstyle=<layered|clean|hexagonal>}</li>
 * </ul>
 *
 * <p>The task:
 * <ol>
 *   <li>reads the chosen style and resolves the source skeleton under
 *       {@code src-styles/<style>/},</li>
 *   <li>wipes the current skeleton out of {@code boot/src/main/java/}
 *       and {@code boot/src/test/java/} (preserving
 *       {@code application.properties}),</li>
 *   <li>copies the matching skeleton into place,</li>
 *   <li>runs the placeholder substitution on every tracked text file
 *       ({@code __GROUP__}, {@code __ARTIFACT__}, {@code __PACKAGE__},
 *       {@code __PACKAGE_PATH__}, {@code __STYLE__}),</li>
 *   <li>updates {@code gradle.properties} with the new coordinates.</li>
 * </ol>
 *
 * <p>Only files under the project root are processed. The
 * {@code build/}, {@code .gradle/}, {@code gradle/wrapper/} and
 * {@code src-styles/} directories and the {@code gradlew} /
 * {@code gradlew.bat} scripts are excluded to avoid clobbering the
 * local Gradle daemon's transient state.
 *
 * <p>Run once after cloning the template:
 *
 * <pre>
 * ./gradlew rename -PgroupId=com.acme -PartifactId=my-service \
 *     -Ppackage=com.acme.my -Pstyle=layered
 * </pre>
 */
tasks.register("rename") {
    val groupProp: String = (project.findProperty("groupId") as String?)
        ?: error("Missing -PgroupId=<your.group>")
    val artifactProp: String = (project.findProperty("artifactId") as String?)
        ?: error("Missing -PartifactId=<your-artifact>")
    val packageProp: String = (project.findProperty("package") as String?)
        ?: error("Missing -Ppackage=<your.java.package>")
    val styleProp: String = (project.findProperty("style") as String?)
        ?: "layered"
    val style: NovaStyle = NovaStyle.fromIdentifier(styleProp)

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
        "src-styles/**",
        "gradlew",
        "gradlew.bat"
    )

    doLast {
        val rootDir: Path = rootProject.projectDir.toPath()
        val filesTouched = mutableSetOf<Path>()

        // Step 1: copy the matching skeleton into boot/src/{main,test}/java
        val styleSource: Path = rootDir.resolve("src-styles/${style.folder}")
        if (!Files.isDirectory(styleSource)) {
            error("Style skeleton not found: $styleSource")
        }
        val bootMainJava = rootDir.resolve("boot/src/main/java/__PACKAGE__")
        val bootTestJava = rootDir.resolve("boot/src/test/java/__PACKAGE__")
        val productMainJava = rootDir.resolve("product/src/main/java/__PACKAGE__")
        val sharedMainJava = rootDir.resolve("shared/src/main/java/__PACKAGE__")

        // Wipe previously installed skeletons (Hexagonal variant may have shipped
        // with product/ and shared/ — keep shared/ contents if it exists, since
        // it's style-agnostic).
        deleteRecursively(bootMainJava)
        deleteRecursively(bootTestJava)
        deleteRecursively(productMainJava)

        // Copy style-specific sources for boot/.
        copyRecursively(styleSource.resolve("boot/src/main/java/__PACKAGE__"), bootMainJava)
        copyRecursively(styleSource.resolve("boot/src/test/java/__PACKAGE__"), bootTestJava)

        // Copy style-specific resources for boot/.
        val styleResources = styleSource.resolve("boot/src/main/resources")
        if (Files.isDirectory(styleResources)) {
            val bootResources = rootDir.resolve("boot/src/main/resources")
            deleteRecursively(bootResources)
            copyRecursively(styleResources, bootResources)
        }

        // Copy style-specific sources for product/ (only hexagonal/clean have it).
        val styleProduct = styleSource.resolve("product/src/main/java/__PACKAGE__")
        if (Files.isDirectory(styleProduct)) {
            copyRecursively(styleProduct, productMainJava)
        }

        // Restore shared/ skeleton — it's style-agnostic and ships at the root.
        val sharedSource = rootDir.resolve("src-styles/shared-resources/java/__PACKAGE__")
        if (Files.isDirectory(sharedSource)) {
            deleteRecursively(sharedMainJava)
            copyRecursively(sharedSource, sharedMainJava)
        }

        // Step 2: placeholder substitution on every tracked text file.
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
                    .replace("__STYLE__", style.folder)
                if (rewritten != original) {
                    f.writeText(rewritten)
                    filesTouched.add(f.toPath())
                }
            }

        // Step 3: update gradle.properties with the new coordinates while
        // preserving any other keys the file already carries (Quarkus
        // platform coords, Nova extension version, toolchain flags, etc.).
        val propsFile = rootDir.resolve("gradle.properties").toFile()
        val props = Properties().apply {
            propsFile.inputStream().use { stream -> load(stream) }
        }
        props.setProperty("group", groupProp)
        props.setProperty("artifactId", artifactProp)
        props.setProperty("version", "0.1.0-SNAPSHOT")
        props.setProperty("style", style.folder)
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
        println("   style     = ${style.folder} (${style.description})")
        println("   files touched: ${filesTouched.size}")
        println("   gradle.properties updated with new coordinates.")
        println()
        println("Next steps:")
        println("  1. git add -A && git commit -m \"rename: $artifactProp (${style.folder})\"")
        println("  2. ./gradlew build")
        println("  3. ./gradlew :boot:quarkusDev")
    }
}

/** Recursive delete helper for the rename task. */
fun deleteRecursively(path: Path) {
    if (!Files.exists(path)) return
    if (Files.isDirectory(path)) {
        Files.walk(path)
            .sorted(Comparator.reverseOrder())
            .forEach { Files.delete(it) }
    } else {
        Files.delete(path)
    }
}

/** Recursive copy helper for the rename task. */
fun copyRecursively(source: Path, target: Path) {
    if (!Files.exists(source)) return
    Files.walk(source).forEach { src ->
        val dst = target.resolve(source.relativize(src).toString())
        if (Files.isDirectory(src)) {
            Files.createDirectories(dst)
        } else {
            Files.createDirectories(dst.parent)
            Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING)
        }
    }
}