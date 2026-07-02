package com.edgarkirk.unitconv.persistence;

import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ConversionResultRepositoryTest {

    @Autowired
    private ConversionResultRepository conversionResultRepository;

    @Test
    void should_persistConversionResultAndGenerateUuid_when_saved() {
        ConversionResult conversionResult = new ConversionResult();
        conversionResult.setInputValue(new BigDecimal("10"));
        conversionResult.setSourceUnit("metres");
        conversionResult.setTargetUnit("feet");
        conversionResult.setResult(new BigDecimal("32.808400"));

        ConversionResult saved = conversionResultRepository.save(conversionResult);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getInputValue()).isEqualByComparingTo("10");
        assertThat(saved.getSourceUnit()).isEqualTo("metres");
        assertThat(saved.getTargetUnit()).isEqualTo("feet");
        assertThat(saved.getResult()).isEqualByComparingTo("32.808400");
    }
}
