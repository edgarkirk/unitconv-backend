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
    void should_persistConversionResult_withGeneratedId_when_save() {
        ConversionResult saved = conversionResultRepository.saveAndFlush(
                new ConversionResult(BigDecimal.valueOf(12.5), "metres", "feet", BigDecimal.valueOf(41.0105)));

        assertThat(saved.getId()).isNotNull();
        assertThat(conversionResultRepository.findById(saved.getId())).isPresent();
    }
}
