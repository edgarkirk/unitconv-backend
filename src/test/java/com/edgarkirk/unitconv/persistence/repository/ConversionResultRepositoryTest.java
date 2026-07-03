package com.edgarkirk.unitconv.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

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
    void should_persistConversionResult_when_save() {
        UUID id = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        ConversionResult saved = conversionResultRepository.save(new ConversionResult(id, new BigDecimal("2"), "metres", "feet", new BigDecimal("6.561679790026246")));

        assertThat(conversionResultRepository.findById(saved.id())).isPresent();
        assertThat(conversionResultRepository.count()).isEqualTo(1);
    }
}
