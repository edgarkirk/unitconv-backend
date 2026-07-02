package com.edgarkirk.unitconv.persistence.repository;

import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class ConversionResultRepositoryTest {

    @Autowired
    private ConversionResultRepository conversionResultRepository;

    @Test
    void should_persistConversionResult_when_saved() {
        ConversionResult result = new ConversionResult(new BigDecimal("10"), "metres", "feet", new BigDecimal("32.8084"));

        ConversionResult saved = conversionResultRepository.saveAndFlush(result);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getInputValue()).isEqualByComparingTo("10");
    }

    @Test
    void should_retrieveConversionResult_when_savedEntityExists() {
        ConversionResult saved = conversionResultRepository.saveAndFlush(
                new ConversionResult(new BigDecimal("5"), "litres", "gallons", new BigDecimal("1.32086")));

        assertThat(conversionResultRepository.findById(saved.getId())).isPresent();
    }
}
