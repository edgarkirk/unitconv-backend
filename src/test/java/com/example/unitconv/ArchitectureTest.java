package com.example.unitconv;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchitectureTest {

    private static final String BASE_PACKAGE = "com.example.unitconv";

    private static final com.tngtech.archunit.core.domain.JavaClasses CLASSES = new ClassFileImporter()
        .withImportOption(new ImportOption.DoNotIncludeTests())
        .importPackages(BASE_PACKAGE);

    @Test
    void controllers_must_not_access_repositories_directly() {
        noClasses().that().resideInAPackage("..api.controller..")
            .should().dependOnClassesThat().resideInAPackage("..repository..")
            .check(CLASSES);
    }

    @Test
    void services_must_not_depend_on_controllers() {
        noClasses().that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAPackage("..api.controller..")
            .check(CLASSES);
    }

    @Test
    void repositories_must_not_depend_on_services_or_controllers() {
        noClasses().that().resideInAPackage("..repository..")
            .should().dependOnClassesThat().resideInAnyPackage("..application..", "..api.controller..")
            .allowEmptyShould(true)
            .check(CLASSES);
    }
}
