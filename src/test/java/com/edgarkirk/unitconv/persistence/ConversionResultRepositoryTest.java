package com.edgarkirk.unitconv.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import com.edgarkirk.unitconv.persistence.entity.ConversionResultEntity;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
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
    void should_persistConversionResultWithGeneratedId_when_save() {
        ConversionResultEntity saved = conversionResultRepository.save(
                new ConversionResultEntity(null, new BigDecimal("10"), "metres", "feet", new BigDecimal("32.8084")));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getInputValue()).isEqualByComparingTo("10");
        assertThat(conversionResultRepository.count()).isEqualTo(1L);
    }
}
