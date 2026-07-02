package com.edgarkirk.unitconv.service.dao;

import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversionResultDaoTest {

    @Mock
    private ConversionResultRepository conversionResultRepository;

    @InjectMocks
    private ConversionResultDao conversionResultDao;

    @Test
    void should_saveConversionResult_when_repositorySavesEntity() {
        ConversionResult result = new ConversionResult(new BigDecimal("10"), "metres", "feet", new BigDecimal("32.8084"));
        ConversionResult persisted = new ConversionResult(new BigDecimal("10"), "metres", "feet", new BigDecimal("32.8084"));

        when(conversionResultRepository.save(result)).thenReturn(persisted);

        ConversionResult saved = conversionResultDao.save(result);

        assertThat(saved).isSameAs(persisted);
    }

    @Test
    void should_returnSavedResult_when_repositoryFindsById() {
        UUID id = UUID.randomUUID();
        ConversionResult result = new ConversionResult(new BigDecimal("5"), "litres", "gallons", new BigDecimal("1.32086"));

        when(conversionResultRepository.findById(id)).thenReturn(Optional.of(result));

        Optional<ConversionResult> found = conversionResultDao.findById(id);

        assertThat(found).contains(result);
    }
}
