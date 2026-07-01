package com.edgarkirk.unitconv.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ConversionRepositoryTest {

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ConversionResultRepository conversionResultRepository;

    @Test
    void should_loadSixSupportedUnits_fromSeedData() {
        final List<Unit> units = unitRepository.findAll();

        assertThat(units).hasSize(6);
        final Set<String> names = units.stream().map(Unit::getName).collect(Collectors.toSet());
        assertThat(names).containsExactlyInAnyOrder("metres", "feet", "kilometres", "miles", "litres", "gallons");
    }

    @Test
    void should_persistConversionResult_when_saved() {
        final ConversionResult saved = conversionResultRepository.save(
                new ConversionResult(new BigDecimal("1"), "metres", "feet", new BigDecimal("3.280840")));

        assertThat(saved.getId()).isNotNull();
        assertThat(conversionResultRepository.findAll()).hasSize(1);
        final ConversionResult persisted = conversionResultRepository.findAll().get(0);
        assertThat(persisted.getInputValue()).isEqualByComparingTo("1");
        assertThat(persisted.getSourceUnit()).isEqualTo("metres");
        assertThat(persisted.getTargetUnit()).isEqualTo("feet");
        assertThat(persisted.getResult()).isEqualByComparingTo("3.280840");
    }
}
