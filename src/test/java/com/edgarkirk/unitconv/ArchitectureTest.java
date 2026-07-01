package com.edgarkirk.unitconv;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.edgarkirk.unitconv", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule controllers_must_not_access_repositories_directly = noClasses()
            .that().resideInAPackage("..api..")
            .should().dependOnClassesThat().resideInAPackage("..persistence.repository..");

    @ArchTest
    static final ArchRule services_must_not_depend_on_controllers = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAPackage("..api..");

    @ArchTest
    static final ArchRule repositories_must_not_depend_on_services = noClasses()
            .that().resideInAPackage("..persistence.repository..")
            .should().dependOnClassesThat().resideInAPackage("..application..");

    @ArchTest
    static final ArchRule persistence_should_not_depend_on_dtos = noClasses()
            .that().resideInAPackage("..persistence..")
            .should().dependOnClassesThat().resideInAPackage("..dto..");
}
