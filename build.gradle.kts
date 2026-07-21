import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.Properties

/**
 * Root build file for a Nova Platform microservice instance generated
 * from the nova-java-quarkus-template.
 *
 * <p>Single-module layout: the generated service IS the root project.
 * All Java toolchain, repository and Quarkus wiring lives in the
 * convention plugins under buildSrc (nova.java-conventions,
 * nova.repositories-conventions, nova.quarkus-conventions). This file
 * only applies nova.quarkus-conventions, declares group/version and
 * hosts the {@code rename} task used to instantiate the template.
 *
 * <pre>
 * ./gradlew rename \
 *     -PgroupId=pe.utp.nova \
 *     -PartifactId=ms-academic-course \
 *     -Ppackage=pe.utp.nova \
 *     -Pdomain=academic \
 *     -Ptype=service
 * </pre>
 *
 * <p>{@code -Ptype=} selects the archetype ({@code service}, {@code bff}
 * or {@code acl}). {@code -Pdomain=} is the microservice / bounded
 * context name (CoRD style, e.g. pricing) — it becomes the
 * {@code __DOMAIN__} package that wraps the domain code. Optionally
 * {@code -PoutputDir=<path>} generates into a separate directory,
 * leaving this template untouched.
 */
plugins {
    id("nova.quarkus-conventions")
}

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()

/**
 * Archetypes supported by the template. Each value maps to a folder
 * under {@code src-styles/} containing the skeleton that the
 * {@code rename} task copies into the generated project.
 */
enum class NovaArchetype(val folder: String, val description: String) {
    SERVICE("service", "Microservice — Hexagonal + DDD (adapter/domain/port/service under a domain package)"),
    BFF("bff", "Backend for Frontend — API composition over downstream services"),
    ACL("acl", "Anti-Corruption Layer — isolates an external/legacy system");

    companion object {
        fun fromIdentifier(id: String): NovaArchetype =
            values().firstOrNull { it.folder == id.lowercase() }
                ?: error("Unknown type '$id'. "
                        + "Valid values: ${values().joinToString { it.folder }}")
    }
}

/**
 * `rename` task — instantiates the template into a concrete project.
 *
 * <p>All parameter reads and the -P validations live INSIDE doLast, so
 * a normal `./gradlew build` never evaluates them — only an explicit
 * `./gradlew rename ...` does.
 *
 * <p>Parameters:
 * <ul>
 *   <li>{@code -PgroupId=<your.group>}</li>
 *   <li>{@code -PartifactId=<your-artifact>}</li>
 *   <li>{@code -Ppackage=<your.java.package>}</li>
 *   <li>{@code -Pdomain=<domain-name>} — the bounded-context package
 *       (e.g. academic); required.</li>
 *   <li>{@code -Ptype=<service|bff|acl>} (defaults to service)</li>
 *   <li>{@code -PoutputDir=<path>} (optional)</li>
 * </ul>
 */
tasks.register("rename") {
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
        "buildSrc/build/**",
        "buildSrc/.gradle/**",
        ".gradle/**",
        "gradle/wrapper/**",
        "src-styles/**",
        "gradlew",
        "gradlew.bat"
    )

    doLast {
        // Parameter reads live here (not at configuration time) so that a
        // plain build never triggers these validations.
        val groupProp: String = (project.findProperty("groupId") as String?)
            ?: error("Missing -PgroupId=<your.group>")
        val artifactProp: String = (project.findProperty("artifactId") as String?)
            ?: error("Missing -PartifactId=<your-artifact>")
        val packageProp: String = (project.findProperty("package") as String?)
            ?: error("Missing -Ppackage=<your.java.package>")
        val domainProp: String = (project.findProperty("domain") as String?)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: error("Missing -Pdomain=<domain-name> (the bounded-context package, e.g. academic)")
        val typeProp: String = (project.findProperty("type") as String?)
            ?: "service"
        val archetype: NovaArchetype = NovaArchetype.fromIdentifier(typeProp)
        val outputDirProp: String? = project.findProperty("outputDir") as String?
        val packagePath = packageProp.replace('.', '/')

        val sourceRoot: Path = rootProject.projectDir.toPath()

        // Directory segment names that must never be copied to the output
        // dir — Gradle's transient state at ANY depth (including inside
        // buildSrc) holds locks that would break the copy.
        val skipSegments = setOf("build", ".gradle", ".git")

        // Resolve the working root. With -PoutputDir the template is
        // copied to the target first (skipping transient state at any
        // level) and the rename operates on that copy; otherwise it runs
        // in place.
        val rootDir: Path = if (outputDirProp != null) {
            val target = Paths.get(outputDirProp).toAbsolutePath().normalize()
            if (target == sourceRoot.toAbsolutePath().normalize()) {
                error("-PoutputDir cannot be the template directory itself")
            }
            Files.walk(sourceRoot).forEach { src ->
                val rel = sourceRoot.relativize(src)
                val skip = (0 until rel.nameCount).any {
                    skipSegments.contains(rel.getName(it).toString())
                }
                if (skip) return@forEach
                val dst = target.resolve(rel.toString())
                if (Files.isDirectory(src)) {
                    Files.createDirectories(dst)
                } else {
                    Files.createDirectories(dst.parent)
                    Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING)
                }
            }
            println("   outputDir = $target (generated into a copy, template left intact)")
            target
        } else {
            sourceRoot
        }

        val filesTouched = mutableSetOf<Path>()

        // Step 1: copy the chosen archetype skeleton into src/main + src/test.
        val archetypeSource: Path = rootDir.resolve("src-styles/${archetype.folder}")
        if (!Files.isDirectory(archetypeSource)) {
            error("Archetype skeleton not found: $archetypeSource")
        }

        val mainJava = rootDir.resolve("src/main/java/__PACKAGE__")
        val testJava = rootDir.resolve("src/test/java/__PACKAGE__")
        val mainResources = rootDir.resolve("src/main/resources")

        // Wipe any previously installed skeleton so re-running rename with
        // a different type produces a clean tree.
        deleteRecursively(mainJava)
        deleteRecursively(testJava)

        // Copy archetype sources.
        copyRecursively(archetypeSource.resolve("src/main/java/__PACKAGE__"), mainJava)
        copyRecursively(archetypeSource.resolve("src/test/java/__PACKAGE__"), testJava)

        // Copy archetype resources (application.properties, etc.).
        val archetypeResources = archetypeSource.resolve("src/main/resources")
        if (Files.isDirectory(archetypeResources)) {
            deleteRecursively(mainResources)
            copyRecursively(archetypeResources, mainResources)
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
                    .replace("__DOMAIN__", domainProp)
                    .replace("__TYPE__", archetype.folder)
                if (rewritten != original) {
                    f.writeText(rewritten)
                    filesTouched.add(f.toPath())
                }
            }

        // Step 3: rename placeholder DOMAIN directories to the domain name
        // (e.g. __DOMAIN__ -> academic). Done before the package rename so
        // the domain folder ends up under the real package path.
        renameDirPlaceholder(rootDir.resolve("src/main/java/__PACKAGE__"), "__DOMAIN__", domainProp)
        renameDirPlaceholder(rootDir.resolve("src/test/java/__PACKAGE__"), "__DOMAIN__", domainProp)

        // Step 4: rename the placeholder package directories to the real
        // package path (e.g. __PACKAGE__ -> pe/utp/nova).
        renamePackageDir(rootDir.resolve("src/main/java"), packagePath)
        renamePackageDir(rootDir.resolve("src/test/java"), packagePath)

        // Step 5: update gradle.properties with the new coordinates while
        // preserving the other keys (Quarkus platform coords, Nova
        // versions, toolchain flags).
        val propsFile = rootDir.resolve("gradle.properties").toFile()
        val props = Properties().apply {
            propsFile.inputStream().use { stream -> load(stream) }
        }
        props.setProperty("group", groupProp)
        props.setProperty("artifactId", artifactProp)
        props.setProperty("version", "0.1.0-SNAPSHOT")
        props.setProperty("domain", domainProp)
        props.setProperty("type", archetype.folder)
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
        println("   domain    = $domainProp")
        println("   type      = ${archetype.folder} (${archetype.description})")
        println("   files touched: ${filesTouched.size}")
        println("   gradle.properties updated with new coordinates.")
        println()
        println("Next steps:")
        println("  1. git add -A && git commit -m \"rename: $artifactProp ($domainProp, ${archetype.folder})\"")
        println("  2. ./gradlew build")
        println("  3. ./gradlew quarkusDev")
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

/**
 * Renames a single placeholder directory (e.g. {@code __DOMAIN__}) to a
 * real name within {@code parent}, moving its contents. No-op if the
 * placeholder directory does not exist.
 */
fun renameDirPlaceholder(parent: Path, placeholderName: String, realName: String) {
    val placeholder = parent.resolve(placeholderName)
    if (!Files.isDirectory(placeholder)) return
    val target = parent.resolve(realName)
    Files.createDirectories(target)
    copyRecursively(placeholder, target)
    deleteRecursively(placeholder)
}

/**
 * Moves the contents of {@code <sourceRoot>/__PACKAGE__} into
 * {@code <sourceRoot>/<packagePath>} and deletes the placeholder dir,
 * so the on-disk package matches the declared Java package.
 */
fun renamePackageDir(sourceRoot: Path, packagePath: String) {
    val placeholder = sourceRoot.resolve("__PACKAGE__")
    if (!Files.isDirectory(placeholder)) return
    val target = sourceRoot.resolve(packagePath)
    Files.createDirectories(target)
    copyRecursively(placeholder, target)
    deleteRecursively(placeholder)
}