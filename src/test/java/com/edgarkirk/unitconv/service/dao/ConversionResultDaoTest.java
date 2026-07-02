package com.edgarkirk.unitconv.service.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConversionResultDaoTest {

    @Mock
    private ConversionResultRepository conversionResultRepository;

    @InjectMocks
    private ConversionResultDaoImpl conversionResultDao;

    @Test
    void should_persistConversionResult_when_repositorySavesEntity() {
        // Arrange
        ConversionResult conversionResult = new ConversionResult(
                UUID.randomUUID(),
                new BigDecimal("10"),
                "metres",
                "feet",
                new BigDecimal("32.808400"));
        when(conversionResultRepository.save(conversionResult)).thenReturn(conversionResult);

        // Act
        ConversionResult actual = conversionResultDao.save(conversionResult);

        // Assert
        assertThat(actual).isEqualTo(conversionResult);
        verify(conversionResultRepository).save(conversionResult);
    }
}
