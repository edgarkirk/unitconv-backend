package com.edgarkirk.unitconv.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import java.math.BigDecimal;
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
    void should_persistConversionResult_when_entityIsSaved() {
        // Arrange
        ConversionResult conversionResult = new ConversionResult(
                null,
                new BigDecimal("10"),
                "metres",
                "feet",
                new BigDecimal("32.808400"));

        // Act
        ConversionResult saved = conversionResultRepository.saveAndFlush(conversionResult);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(conversionResultRepository.findById(saved.getId())).isPresent();
    }
}
