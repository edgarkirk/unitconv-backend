package com.edgarkirk.unitconv.persistence.repository;

import com.edgarkirk.unitconv.persistence.entity.Unit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UnitRepositoryTest {

    @Autowired
    private UnitRepository unitRepository;

    @Test
    void should_returnAllSeededUnits_inDeterministicOrder() {
        var units = unitRepository.findAllByOrderByNameAsc();

        assertThat(units).extracting(Unit::getName)
                .containsExactly("feet", "gallons", "kilometres", "litres", "metres", "miles");
    }

    @Test
    void should_persistUnit_whenSavingNewRecord() {
        var saved = unitRepository.save(new Unit(null, "yards", "imperial"));

        assertThat(saved.getId()).isNotNull();
        assertThat(unitRepository.findById(saved.getId())).isPresent();
    }
}
