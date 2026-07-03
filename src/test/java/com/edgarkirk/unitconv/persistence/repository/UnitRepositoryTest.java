package com.edgarkirk.unitconv.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

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
    void should_returnSeededUnitsInNameOrder_when_findAllByOrderByNameAsc() {
        List<Unit> units = unitRepository.findAllByOrderByNameAsc();

        assertThat(units).extracting(Unit::name)
                .containsExactly("feet", "gallons", "kilometres", "litres", "metres", "miles");
    }
}
