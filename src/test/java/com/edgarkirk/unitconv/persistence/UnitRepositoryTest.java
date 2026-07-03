package com.edgarkirk.unitconv.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.entity.Unit;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UnitRepositoryTest {

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ConversionResultRepository conversionResultRepository;

    @Test
    void should_findSeededUnit_when_lookupByName() {
        Optional<Unit> unit = unitRepository.findByName("metres");

        assertThat(unit).isPresent();
        assertThat(unit).get().satisfies(found -> {
            assertThat(found.getName()).isEqualTo("metres");
            assertThat(found.getSystem()).isEqualTo("metric");
        });
    }

    @Test
    void should_persistConversionResult_when_saved() {
        ConversionResult saved = conversionResultRepository.saveAndFlush(
                new ConversionResult(null, new BigDecimal("10"), "metres", "feet", new BigDecimal("32.8")));

        assertThat(saved.getId()).isNotNull();
        assertThat(conversionResultRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void should_returnSixSeededUnits_when_listingAllUnits() {
        assertThat(unitRepository.findAll()).hasSize(6);
    }
}
