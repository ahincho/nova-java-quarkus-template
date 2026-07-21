package __PACKAGE__;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Enforces the Hexagonal dependency rule with ArchUnit: dependencies
 * point inward (web -> application -> domain), never outward.
 */
class ArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("__PACKAGE__");
    }

    @Test
    void hexagonalLayersRespectDependencyRule() {
        Architectures.layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Domain").definedBy("__PACKAGE__.domain..")
                .layer("Application").definedBy("__PACKAGE__.application..")
                .layer("Infrastructure").definedBy("__PACKAGE__.infrastructure..")
                .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
                .check(classes);
    }

    @Test
    void domainDependsOnNothingOutward() {
        com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses()
                .that().resideInAPackage("__PACKAGE__.domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "__PACKAGE__.application..",
                        "__PACKAGE__.infrastructure..")
                .check(classes);
    }
}