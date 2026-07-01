package com.edgarkirk.unitconv.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

class ArchitectureTest {

    private static final JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.edgarkirk.unitconv");

    @Test
    void controllers_must_not_access_persistence_directly() {
        ArchRuleDefinition.noClasses().that().resideInAPackage("..api..")
                .should().dependOnClassesThat().resideInAPackage("..persistence..")
                .check(classes);
    }

    @Test
    void services_must_not_depend_on_api() {
        ArchRuleDefinition.noClasses().that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAPackage("..api..")
                .check(classes);
    }

    @Test
    void dto_must_not_depend_on_api_or_application() {
        ArchRuleDefinition.noClasses().that().resideInAPackage("..dto..")
                .should().dependOnClassesThat().resideInAnyPackage("..api..", "..application..")
                .check(classes);
    }
}
