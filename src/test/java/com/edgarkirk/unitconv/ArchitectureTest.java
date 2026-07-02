package com.edgarkirk.unitconv;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.edgarkirk.unitconv", importOptions = DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule controllers_must_not_access_repositories_directly = noClasses()
            .that().resideInAPackage("..api..")
            .should().dependOnClassesThat().resideInAPackage("..persistence.repository..")
            .because("controllers must go through services");

    @ArchTest
    static final ArchRule controllers_must_not_access_daos_directly = noClasses()
            .that().resideInAPackage("..api..")
            .should().dependOnClassesThat().resideInAPackage("..service.dao..")
            .because("controllers must go through services");

    @ArchTest
    static final ArchRule dto_must_not_depend_on_persistence = noClasses()
            .that().resideInAPackage("..dto..")
            .should().dependOnClassesThat().resideInAPackage("..persistence..")
            .because("DTOs must stay at the edge of the application");

    @ArchTest
    static final ArchRule repositories_must_not_depend_on_api = noClasses()
            .that().resideInAPackage("..persistence.repository..")
            .should().dependOnClassesThat().resideInAPackage("..api..")
            .because("repositories must only depend on persistence concerns");
}
