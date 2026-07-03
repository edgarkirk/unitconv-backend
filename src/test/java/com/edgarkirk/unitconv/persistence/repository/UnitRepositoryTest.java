package com.edgarkirk.unitconv.persistence.repository;

import com.edgarkirk.unitconv.persistence.entity.Unit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UnitRepositoryTest {

    @Autowired
    private UnitRepository unitRepository;

    @Test
    void should_returnSeededUnits_when_findAll() {
        List<Unit> units = unitRepository.findAll();

        assertThat(units).hasSize(6);
        assertThat(units).extracting(Unit::getName)
                .containsExactly("metres", "feet", "kilometres", "miles", "litres", "gallons");
        assertThat(units).extracting(Unit::getSystem)
                .containsExactly("metric", "imperial", "metric", "imperial", "metric", "imperial");
    }

    @Test
    void should_returnUnit_when_findByName() {
        Unit unit = unitRepository.findByName("metres").orElseThrow();

        assertThat(unit.getName()).isEqualTo("metres");
        assertThat(unit.getSystem()).isEqualTo("metric");
    }
}
