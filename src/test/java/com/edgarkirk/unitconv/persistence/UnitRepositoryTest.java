package com.edgarkirk.unitconv.persistence;

import com.edgarkirk.unitconv.persistence.entity.Unit;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
    void should_returnAllPreloadedUnits_when_findAllByOrderByNameAsc() {
        List<Unit> units = unitRepository.findAllByOrderByNameAsc();

        assertThat(units).hasSize(6);
        assertThat(units).extracting(Unit::getName)
                .containsExactly("feet", "gallons", "kilometres", "litres", "metres", "miles");
    }

    @Test
    void should_findMetresByName_when_lookupByName() {
        assertThat(unitRepository.findByName("metres")).isPresent();
        assertThat(unitRepository.findByName("metres").orElseThrow().getSystem()).isEqualTo("metric");
    }
}
