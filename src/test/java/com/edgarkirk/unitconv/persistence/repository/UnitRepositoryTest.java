package com.edgarkirk.unitconv.persistence.repository;

import com.edgarkirk.unitconv.persistence.entity.Unit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class UnitRepositoryTest {

    @Autowired
    private UnitRepository unitRepository;

    @Test
    void should_returnAllSeededUnits_when_repositoryIsLoaded() {
        assertThat(unitRepository.findAll()).hasSize(6);
    }

    @Test
    void should_findUnitByName_when_unitExists() {
        Optional<Unit> unit = unitRepository.findByName("metres");

        assertThat(unit).isPresent();
        assertThat(unit).get().extracting(Unit::getName, Unit::getSystem)
                .containsExactly("metres", "metric");
    }

    @Test
    void should_enforceUniqueNameConstraint_when_duplicateUnitIsPersisted() {
        Unit duplicate = new Unit("metres", "metric");

        assertThatThrownBy(() -> unitRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
