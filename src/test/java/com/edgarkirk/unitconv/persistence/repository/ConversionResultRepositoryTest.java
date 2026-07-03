package com.edgarkirk.unitconv.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.edgarkirk.unitconv.persistence.entity.ConversionResultEntity;

@DataJpaTest
@ActiveProfiles("test")
class ConversionResultRepositoryTest {

    @Autowired
    private ConversionResultRepository conversionResultRepository;

    @Test
    void should_persistConversionResult_withGeneratedUuid_when_saved() {
        ConversionResultEntity saved = conversionResultRepository.save(
                new ConversionResultEntity(new BigDecimal("10"), "metres", "feet", new BigDecimal("32.8084")));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getInputValue()).isEqualByComparingTo("10");
        assertThat(conversionResultRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void should_startEmpty_when_repositoryIsInitialized() {
        assertThat(conversionResultRepository.findAll()).isEmpty();
    }
}
