package com.edgarkirk.unitconv.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.api.dto.response.UnitResponse;
import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.entity.Unit;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitPairException;
import com.edgarkirk.unitconv.service.exception.UnitNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConversionServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ConversionResultRepository conversionResultRepository;

    private ConversionService conversionService;

    @BeforeEach
    void setUp() {
        conversionService = new ConversionServiceImpl(unitRepository, conversionResultRepository);
    }

    @Test
    void should_returnConversionResult_when_validInput() {
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "metres", "metric")));
        when(unitRepository.findByName("feet")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "feet", "imperial")));
        when(conversionResultRepository.save(any(ConversionResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ConversionResultResponse response = conversionService.convert(new ConversionRequest(new BigDecimal("1"), "metres", "feet"));

        assertThat(response.inputValue()).isEqualByComparingTo("1");
        assertThat(response.sourceUnit()).isEqualTo("metres");
        assertThat(response.targetUnit()).isEqualTo("feet");
        assertThat(response.result()).isEqualByComparingTo("3.280839895013123");
    }

    @Test
    void should_convertBackWithinPrecision_when_reverseConversion() {
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "metres", "metric")));
        when(unitRepository.findByName("feet")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "feet", "imperial")));
        when(conversionResultRepository.save(any(ConversionResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ConversionResultResponse toFeet = conversionService.convert(new ConversionRequest(new BigDecimal("1"), "metres", "feet"));
        ConversionResultResponse backToMetres = conversionService.convert(new ConversionRequest(toFeet.result(), "feet", "metres"));

        assertThat(backToMetres.result()).isCloseTo(new BigDecimal("1"), within(new BigDecimal("0.000000000000001")));
    }

    @Test
    void should_throwWhenUnitsAreIncompatible_when_crossGroupConversion() {
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "metres", "metric")));
        when(unitRepository.findByName("gallons")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "gallons", "imperial")));

        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(new BigDecimal("1"), "metres", "gallons")))
                .isInstanceOf(IncompatibleUnitPairException.class)
                .hasMessage("Units metres and gallons are incompatible");
    }

    @Test
    void should_throwWhenSourceUnitIsMissing_when_lookupFails() {
        when(unitRepository.findByName("furlongs")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(new BigDecimal("1"), "furlongs", "metres")))
                .isInstanceOf(UnitNotFoundException.class)
                .hasMessage("Unknown source unit: furlongs");
    }

    @Test
    void should_throwWhenTargetUnitIsMissing_when_lookupFails() {
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "metres", "metric")));
        when(unitRepository.findByName("furlongs")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(new BigDecimal("1"), "metres", "furlongs")))
                .isInstanceOf(UnitNotFoundException.class)
                .hasMessage("Unknown target unit: furlongs");
    }

    @Test
    void should_returnUnits_when_listingSupportedUnits() {
        when(unitRepository.findAllByOrderByNameAsc()).thenReturn(List.of(
                new Unit(UUID.randomUUID(), "feet", "imperial"),
                new Unit(UUID.randomUUID(), "metres", "metric")));

        List<UnitResponse> units = conversionService.listSupportedUnits();

        assertThat(units).extracting(UnitResponse::name).containsExactly("feet", "metres");
    }
}
