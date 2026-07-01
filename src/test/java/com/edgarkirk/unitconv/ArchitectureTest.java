package com.edgarkirk.unitconv;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.domain.JavaClasses;
import org.junit.jupiter.api.Test;

class ArchitectureTest {

    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(new ImportOption.DoNotIncludeTests())
            .importPackages("com.edgarkirk.unitconv");

    @Test
    void controllers_must_not_access_persistence_directly() {
        noClasses()
                .that().resideInAPackage("..api..")
                .should().dependOnClassesThat().resideInAPackage("..persistence..")
                .check(CLASSES);
    }

    @Test
    void services_must_not_depend_on_controllers() {
        noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAPackage("..api..")
                .check(CLASSES);
    }

    @Test
    void repositories_must_not_depend_on_api_or_application_layers() {
        noClasses()
                .that().resideInAPackage("..persistence..")
                .should().dependOnClassesThat().resideInAnyPackage("..api..", "..application..")
                .check(CLASSES);
    }

    @Test
    void dto_package_should_remain_separate_from_api_application_and_persistence_layers() {
        noClasses()
                .that().resideInAPackage("..dto..")
                .should().dependOnClassesThat().resideInAnyPackage("..api..", "..application..", "..persistence..")
                .check(CLASSES);
    }
}
