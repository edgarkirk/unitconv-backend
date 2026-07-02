package com.edgarkirk.unitconv.service.dao;

import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversionResultDaoTest {

    @Mock
    private ConversionResultRepository conversionResultRepository;

    @InjectMocks
    private ConversionResultDao conversionResultDao;

    @Test
    void should_delegateSaveToRepository() {
        ConversionResult conversionResult = new ConversionResult();
        conversionResult.setInputValue(new BigDecimal("10"));
        conversionResult.setSourceUnit("metres");
        conversionResult.setTargetUnit("feet");
        conversionResult.setResult(new BigDecimal("32.808400"));

        ConversionResult saved = new ConversionResult();
        saved.setId(UUID.randomUUID());
        saved.setInputValue(conversionResult.getInputValue());
        saved.setSourceUnit(conversionResult.getSourceUnit());
        saved.setTargetUnit(conversionResult.getTargetUnit());
        saved.setResult(conversionResult.getResult());
        when(conversionResultRepository.save(conversionResult)).thenReturn(saved);

        ConversionResult result = conversionResultDao.save(conversionResult);

        assertThat(result).isSameAs(saved);
        verify(conversionResultRepository).save(conversionResult);
    }
}
