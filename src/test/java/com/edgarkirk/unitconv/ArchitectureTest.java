package com.edgarkirk.unitconv;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchitectureTest {

    private static final com.tngtech.archunit.core.domain.JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(new ImportOption.DoNotIncludeTests())
            .importPackages("com.edgarkirk.unitconv");

    @Test
    void controllers_must_not_access_persistence_directly() {
        noClasses().that().resideInAPackage("..api..")
                .should().dependOnClassesThat().resideInAPackage("..persistence..").check(CLASSES);
    }

    @Test
    void repositories_must_not_depend_on_service_or_api_layers() {
        noClasses().that().resideInAPackage("..persistence.repository..")
                .should().dependOnClassesThat().resideInAnyPackage("..service..", "..api..").check(CLASSES);
    }
}
