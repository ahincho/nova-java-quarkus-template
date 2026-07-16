package __PACKAGE__;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import pe.edu.nova.java.archunit.LayeredArchitectureTest;

/**
 * Architectural compliance test for the Layered variant generated
 * from the {@code nova-java-quarkus-template}.
 *
 * <p>This class extends the abstract
 * {@link LayeredArchitectureTest} provided by the
 * {@code nova-architecture-rules} library, which means every
 * {@code @ArchTest} rule defined there (controller cannot reach
 * repository directly, services cannot depend on controllers,
 * entities stay framework-agnostic, etc.) is automatically enforced
 * by JUnit on this application's compile-time classpath.
 *
 * <p>Run {@code ./gradlew :boot:test} — a violation produces a JUnit
 * failure listing the offending class and the rule that was broken.
 */
@AnalyzeClasses(
        packages = "__PACKAGE__",
        importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest extends LayeredArchitectureTest {

    @Override
    protected String basePackage() {
        return "__PACKAGE__";
    }
}