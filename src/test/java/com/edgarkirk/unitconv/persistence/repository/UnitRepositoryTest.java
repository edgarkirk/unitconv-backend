package com.edgarkirk.unitconv.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;


import com.edgarkirk.unitconv.persistence.entity.Unit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UnitRepositoryTest {

    @Autowired
    private UnitRepository unitRepository;

    @Test
    void should_return_six_supported_units_after_startup_seeding() {
        // Act
        long count = unitRepository.count();

        // Assert
        assertThat(count).isEqualTo(6L);
    }

    @Test
    void should_find_unit_by_name_ignore_case_when_unit_exists() {
        // Arrange
        Unit savedUnit = unitRepository.saveAndFlush(new Unit("metres", "metric"));

        // Act
        var foundUnit = unitRepository.findByNameIgnoreCase("METRES");

        // Assert
        assertThat(foundUnit).isPresent();
        assertThat(foundUnit).get().satisfies(unit -> {
            assertThat(unit.getId()).isEqualTo(savedUnit.getId());
            assertThat(unit.getName()).isEqualTo("metres");
            assertThat(unit.getSystem()).isEqualTo("metric");
        });
    }
}
