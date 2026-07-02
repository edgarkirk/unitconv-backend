package com.edgarkirk.unitconv;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.edgarkirk.unitconv", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule controllers_must_not_access_persistence_directly = noClasses()
            .that().resideInAPackage("..api..")
            .should().dependOnClassesThat().resideInAnyPackage("..persistence..", "..service.dao..");

    @ArchTest
    static final ArchRule services_must_not_depend_on_controllers = noClasses()
            .that().resideInAPackage("..service..")
            .should().dependOnClassesThat().resideInAPackage("..api..");

    @ArchTest
    static final ArchRule daos_must_not_depend_on_api_or_service_implementations = noClasses()
            .that().resideInAPackage("..service.dao..")
            .should().dependOnClassesThat().resideInAnyPackage("..api..", "..service.exception..");

    @ArchTest
    static final ArchRule repositories_must_not_depend_on_upper_layers = noClasses()
            .that().resideInAPackage("..persistence.repository..")
            .should().dependOnClassesThat().resideInAnyPackage("..api..", "..service..", "..dto..", "..mapper..");

    @ArchTest
    static final ArchRule dtos_must_not_depend_on_upper_layers = noClasses()
            .that().resideInAPackage("..dto..")
            .should().dependOnClassesThat().resideInAnyPackage("..api..", "..service..", "..persistence..", "..mapper..");

}
