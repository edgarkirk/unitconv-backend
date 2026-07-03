package com.edgarkirk.unitconv.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ConversionResultRepositoryTest {

    @Autowired
    private ConversionResultRepository conversionResultRepository;

    @Test
    void should_persist_conversion_result_with_generated_id_when_saved() {
        // Arrange
        ConversionResult result = new ConversionResult(new BigDecimal("12.5"), "metres", "feet", new BigDecimal("41.0105"));

        // Act
        ConversionResult savedResult = conversionResultRepository.saveAndFlush(result);

        // Assert
        assertThat(savedResult.getId()).isNotNull();
        assertThat(savedResult.getInputValue()).isEqualByComparingTo(new BigDecimal("12.5"));
        assertThat(savedResult.getSourceUnit()).isEqualTo("metres");
        assertThat(savedResult.getTargetUnit()).isEqualTo("feet");
        assertThat(savedResult.getResult()).isEqualByComparingTo(new BigDecimal("41.0105"));
    }
}
