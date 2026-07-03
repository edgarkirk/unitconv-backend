package com.edgarkirk.unitconv;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchitectureTest {

    private static final JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(new ImportOption.DoNotIncludeTests())
            .importPackages("com.edgarkirk.unitconv");

    @Test
    void controllers_must_not_access_repositories_directly() {
        noClasses().that().resideInAPackage("..api..")
                .should().dependOnClassesThat().resideInAnyPackage("..persistence..", "..service.dao..")
                .check(importedClasses);
    }

    @Test
    void services_must_only_depend_on_allowed_layers() {
        classes().that().resideInAPackage("..service..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "..service..",
                        "..api.dto..",
                        "..persistence..",
                        "java..",
                        "org.slf4j..",
                        "org.springframework..")
                .check(importedClasses);
    }

    @Test
    void repositories_must_not_depend_on_service_or_controller_layers() {
        noClasses().that().resideInAPackage("..persistence.repository..")
                .should().dependOnClassesThat().resideInAnyPackage("..api..", "..service..")
                .check(importedClasses);
    }
}
