package com.edgarkirk.unitconv.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResult;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConversionServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ConversionResultRepository conversionResultRepository;

    @InjectMocks
    private ConversionServiceImpl conversionService;

    @Test
    void should_convert_metres_to_feet_and_persist_result_when_valid_request() {
        // Arrange
        ConversionRequest request = new ConversionRequest(new BigDecimal("12.5"), "metres", "feet");
        when(unitRepository.findByNameIgnoreCase("metres")).thenReturn(Optional.of(new com.edgarkirk.unitconv.persistence.entity.Unit(UUID.randomUUID(), "metres", "metric")));
        when(unitRepository.findByNameIgnoreCase("feet")).thenReturn(Optional.of(new com.edgarkirk.unitconv.persistence.entity.Unit(UUID.randomUUID(), "feet", "imperial")));
        when(conversionResultRepository.save(any(com.edgarkirk.unitconv.persistence.entity.ConversionResult.class)))
                .thenAnswer(invocation -> {
                    com.edgarkirk.unitconv.persistence.entity.ConversionResult entity = invocation.getArgument(0);
                    return new com.edgarkirk.unitconv.persistence.entity.ConversionResult(UUID.randomUUID(), entity.getInputValue(), entity.getSourceUnit(), entity.getTargetUnit(), entity.getResult());
                });

        // Act
        ConversionResult response = conversionService.convert(request);

        // Assert
        assertThat(response.id()).isNotNull();
        assertThat(response.inputValue()).isEqualByComparingTo(new BigDecimal("12.5"));
        assertThat(response.sourceUnit()).isEqualTo("metres");
        assertThat(response.targetUnit()).isEqualTo("feet");
        assertThat(response.result()).isEqualByComparingTo(new BigDecimal("41.0105"));
        verify(conversionResultRepository).save(any(com.edgarkirk.unitconv.persistence.entity.ConversionResult.class));
    }

    @Test
    void should_convert_feet_to_metres_with_round_trip_precision_when_inverse_request_is_submitted() {
        // Arrange
        ConversionRequest request = new ConversionRequest(new BigDecimal("41.0105"), "feet", "metres");
        when(unitRepository.findByNameIgnoreCase("feet")).thenReturn(Optional.of(new com.edgarkirk.unitconv.persistence.entity.Unit(UUID.randomUUID(), "feet", "imperial")));
        when(unitRepository.findByNameIgnoreCase("metres")).thenReturn(Optional.of(new com.edgarkirk.unitconv.persistence.entity.Unit(UUID.randomUUID(), "metres", "metric")));
        when(conversionResultRepository.save(any(com.edgarkirk.unitconv.persistence.entity.ConversionResult.class)))
                .thenAnswer(invocation -> {
                    com.edgarkirk.unitconv.persistence.entity.ConversionResult entity = invocation.getArgument(0);
                    return new com.edgarkirk.unitconv.persistence.entity.ConversionResult(UUID.randomUUID(), entity.getInputValue(), entity.getSourceUnit(), entity.getTargetUnit(), entity.getResult());
                });

        // Act
        ConversionResult response = conversionService.convert(request);

        // Assert
        assertThat(response.inputValue()).isEqualByComparingTo(new BigDecimal("41.0105"));
        assertThat(response.result()).isEqualByComparingTo(new BigDecimal("12.5"));
    }

    @Test
    void should_reject_incompatible_units_when_units_are_from_different_groups() {
        // Arrange
        ConversionRequest request = new ConversionRequest(new BigDecimal("1"), "metres", "gallons");
        when(unitRepository.findByNameIgnoreCase("metres")).thenReturn(Optional.of(new com.edgarkirk.unitconv.persistence.entity.Unit(UUID.randomUUID(), "metres", "metric")));
        when(unitRepository.findByNameIgnoreCase("gallons")).thenReturn(Optional.of(new com.edgarkirk.unitconv.persistence.entity.Unit(UUID.randomUUID(), "gallons", "imperial")));

        // Act & Assert
        assertThatThrownBy(() -> conversionService.convert(request))
                .isInstanceOf(IncompatibleUnitsException.class)
                .hasMessageContaining("incompatible units")
                .hasMessageContaining("metres")
                .hasMessageContaining("gallons");
    }
}
