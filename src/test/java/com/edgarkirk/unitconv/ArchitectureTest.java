package com.edgarkirk.unitconv;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption.Predefined;
import org.junit.jupiter.api.Test;

class ArchitectureTest {

    private static final com.tngtech.archunit.core.domain.JavaClasses classes = new ClassFileImporter()
            .withImportOption(Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.edgarkirk.unitconv");

    @Test
    void no_dao_package_classes_exist() {
        assertThat(classes.stream().noneMatch(javaClass -> javaClass.getPackageName().contains(".service.dao.")))
                .isTrue();
    }

    @Test
    void production_classes_are_loaded() {
        assertThat(classes.stream().anyMatch(javaClass -> javaClass.getPackageName().equals("com.edgarkirk.unitconv.api")))
                .isTrue();
    }
}
