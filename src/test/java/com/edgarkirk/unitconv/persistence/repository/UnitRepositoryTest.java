package com.edgarkirk.unitconv.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.edgarkirk.unitconv.persistence.entity.Unit;
import java.util.List;
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
    void should_findSeededUnits_when_loading_all_supported_units() {
        // Arrange

        // Act
        List<Unit> units = unitRepository.findAll();

        // Assert
        assertThat(units)
                .hasSize(6)
                .extracting(Unit::getName)
                .containsExactlyInAnyOrder("metres", "feet", "kilometres", "miles", "litres", "gallons");
    }

    @Test
    void should_findUnitByName_when_supportedUnitExists() {
        // Arrange

        // Act
        Unit unit = unitRepository.findByName("feet").orElseThrow();

        // Assert
        assertThat(unit.getName()).isEqualTo("feet");
        assertThat(unit.getSystem()).isEqualTo("imperial");
    }
}
