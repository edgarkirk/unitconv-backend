package com.edgarkirk.unitconv.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.persistence.entity.ConversionResultEntity;
import com.edgarkirk.unitconv.persistence.entity.UnitEntity;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import com.edgarkirk.unitconv.service.impl.ConversionServiceImpl;

@ExtendWith(MockitoExtension.class)
class ConversionServiceTest {

    @Mock
    private ConversionResultRepository conversionResultRepository;

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private ConversionServiceImpl conversionService;

    private UnitEntity metres;
    private UnitEntity feet;
    private UnitEntity litres;
    private UnitEntity gallons;

    @BeforeEach
    void setUp() {
        metres = unit("metres", "metric");
        feet = unit("feet", "imperial");
        litres = unit("litres", "metric");
        gallons = unit("gallons", "imperial");
    }

    @Test
    void should_convertMetresToFeet_when_validRequest() {
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(metres));
        when(unitRepository.findByName("feet")).thenReturn(Optional.of(feet));
        when(conversionResultRepository.save(any(ConversionResultEntity.class))).thenAnswer(invocation -> {
            ConversionResultEntity entity = invocation.getArgument(0);
            entity.setId(UUID.randomUUID());
            return entity;
        });

        ConversionResultResponse response = conversionService.convert(
                new ConversionRequest(new BigDecimal("10"), "metres", "feet"));

        assertThat(response).isNotNull();
        assertThat(response.inputValue()).isEqualByComparingTo("10");
        assertThat(response.sourceUnit()).isEqualTo("metres");
        assertThat(response.targetUnit()).isEqualTo("feet");
        assertThat(response.result()).isEqualByComparingTo("32.8084");
        verify(conversionResultRepository).save(any(ConversionResultEntity.class));
    }

    @Test
    void should_rejectIncompatibleUnits_when_sourceAndTargetUnitsAreFromDifferentGroups() {
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(metres));
        when(unitRepository.findByName("gallons")).thenReturn(Optional.of(gallons));

        assertThatThrownBy(() -> conversionService.convert(
                new ConversionRequest(new BigDecimal("10"), "metres", "gallons")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Incompatible units");
    }

    @Test
    void should_roundTripWithinPrecision_when_reverseConversionIsRequested() {
        when(unitRepository.findByName("kilometres")).thenReturn(Optional.of(unit("kilometres", "metric")));
        when(unitRepository.findByName("miles")).thenReturn(Optional.of(unit("miles", "imperial")));
        when(conversionResultRepository.save(any(ConversionResultEntity.class))).thenAnswer(invocation -> {
            ConversionResultEntity entity = invocation.getArgument(0);
            entity.setId(UUID.randomUUID());
            return entity;
        });

        ConversionResultResponse response = conversionService.convert(
                new ConversionRequest(new BigDecimal("3.5"), "kilometres", "miles"));

        assertThat(response).isNotNull();
        assertThat(response.result()).isEqualByComparingTo("2.1748");
    }

    @Test
    void should_persistSavedConversionResult_when_serviceCompletesSuccessfully() {
        when(unitRepository.findByName("litres")).thenReturn(Optional.of(litres));
        when(unitRepository.findByName("gallons")).thenReturn(Optional.of(gallons));
        when(conversionResultRepository.save(any(ConversionResultEntity.class))).thenAnswer(invocation -> {
            ConversionResultEntity entity = invocation.getArgument(0);
            entity.setId(UUID.randomUUID());
            return entity;
        });

        ConversionResultResponse response = conversionService.convert(
                new ConversionRequest(new BigDecimal("1"), "litres", "gallons"));

        assertThat(response.id()).isNotNull();
        verify(conversionResultRepository).save(any(ConversionResultEntity.class));
    }

    private UnitEntity unit(String name, String system) {
        return new UnitEntity(name, system);
    }
}
