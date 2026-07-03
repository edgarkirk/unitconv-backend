package com.edgarkirk.unitconv.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.edgarkirk.unitconv.persistence.entity.UnitEntity;

@DataJpaTest
@ActiveProfiles("test")
class UnitRepositoryTest {

    @Autowired
    private UnitRepository unitRepository;

    @Test
    void should_returnAllSupportedUnits_when_findingAllUnits() {
        List<UnitEntity> units = unitRepository.findAll();

        assertThat(units)
                .hasSize(6)
                .extracting(UnitEntity::getName)
                .containsExactly("metres", "feet", "kilometres", "miles", "litres", "gallons");
    }

    @Test
    void should_returnUnitByCanonicalName_when_lookupByName() {
        assertThat(unitRepository.findByName("metres"))
                .isPresent();
    }

    @Test
    void should_persistUnitWithGeneratedUuid_when_saved() {
        UnitEntity unit = new UnitEntity("yards", "imperial");

        UnitEntity saved = unitRepository.save(unit);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("yards");
    }
}
