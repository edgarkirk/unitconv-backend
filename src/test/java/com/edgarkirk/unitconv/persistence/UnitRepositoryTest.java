package com.edgarkirk.unitconv.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import com.edgarkirk.unitconv.persistence.entity.UnitEntity;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
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
    void should_returnAllSixSeededUnits_when_findAllByOrderByNameAsc() {
        List<UnitEntity> units = unitRepository.findAllByOrderByNameAsc();

        assertThat(units).hasSize(6);
        assertThat(units).extracting(UnitEntity::getName)
                .containsExactly("feet", "gallons", "kilometres", "litres", "metres", "miles");
        assertThat(units).extracting(UnitEntity::getSystem)
                .containsExactly("imperial", "imperial", "metric", "metric", "metric", "imperial");
    }

    @Test
    void should_persistUnit_when_save() {
        UnitEntity saved = unitRepository.save(new UnitEntity(null, "yards", "imperial"));

        assertThat(saved.getId()).isNotNull();
        assertThat(unitRepository.findByName("yards")).isPresent();
    }
}
