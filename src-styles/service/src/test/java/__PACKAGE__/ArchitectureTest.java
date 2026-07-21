package __PACKAGE__;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Enforces the Hexagonal dependency rule with ArchUnit for the domain
 * package (__DOMAIN__): dependencies point inward
 * (adapter -> service/port -> domain), never outward.
 *
 * <p>Only the domain package is validated. Cross-cutting packages at
 * the root (authentication, commons, config, events, utils, web) are
 * NOT part of the hexagonal layers and are excluded — they are shared
 * infrastructure, not part of the bounded context's rings.
 */
class ArchitectureTest {

    private static final String DOMAIN_ROOT = "__PACKAGE__.__DOMAIN__";

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(DOMAIN_ROOT);
    }

    @Test
    void hexagonalLayersRespectDependencyRule() {
        Architectures.layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Domain").definedBy(DOMAIN_ROOT + ".domain..")
                .layer("Application").definedBy(
                        DOMAIN_ROOT + ".service..",
                        DOMAIN_ROOT + ".port..")
                .layer("Adapter").definedBy(DOMAIN_ROOT + ".adapter..")
                .whereLayer("Adapter").mayNotBeAccessedByAnyLayer()
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapter")
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapter")
                .check(classes);
    }

    @Test
    void domainDependsOnNothingOutward() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage(DOMAIN_ROOT + ".domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        DOMAIN_ROOT + ".service..",
                        DOMAIN_ROOT + ".port..",
                        DOMAIN_ROOT + ".adapter..")
                .check(classes);
    }
}