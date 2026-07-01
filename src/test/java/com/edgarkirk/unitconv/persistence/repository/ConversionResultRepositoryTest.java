package com.edgarkirk.unitconv.persistence.repository;

import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ConversionResultRepositoryTest {

    @Autowired
    private ConversionResultRepository conversionResultRepository;

    @Test
    void should_persistConversionResult_withGeneratedId() {
        var saved = conversionResultRepository.save(new ConversionResult(null, BigDecimal.ONE, "metres", "feet", new BigDecimal("3.28084")));

        assertThat(saved.getId()).isNotNull();
        assertThat(conversionResultRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void should_startWithNoConversionResults() {
        assertThat(conversionResultRepository.count()).isZero();
    }
}
