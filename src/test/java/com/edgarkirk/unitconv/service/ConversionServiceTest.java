package com.edgarkirk.unitconv.service;

import static com.edgarkirk.unitconv.TestFixtures.unit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.api.dto.response.UnitResponse;
import com.edgarkirk.unitconv.persistence.entity.ConversionResultEntity;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitsException;
import com.edgarkirk.unitconv.service.exception.UnsupportedUnitException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    void should_roundTripPrecisely_when_convertingMetresToFeetAndBack() {
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(unit("metres", "metric")));
        when(unitRepository.findByName("feet")).thenReturn(Optional.of(unit("feet", "imperial")));
        when(conversionResultRepository.save(any(ConversionResultEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ConversionResultResponse forwardResponse = conversionService.convert(
                new ConversionRequest(new BigDecimal("5"), "metres", "feet"));

        assertThat(forwardResponse.result()).isEqualByComparingTo(new BigDecimal("16.404199"));

        ConversionResultResponse reverseResponse = conversionService.convert(
                new ConversionRequest(forwardResponse.result(), "feet", "metres"));

        assertThat(reverseResponse.result()).isEqualByComparingTo(new BigDecimal("5"));
        verify(conversionResultRepository, times(2)).save(any(ConversionResultEntity.class));
    }

    @Test
    void should_returnConvertedResult_when_validReverseFeetToMetresInput() {
        when(unitRepository.findByName("feet")).thenReturn(Optional.of(unit("feet", "imperial")));
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(unit("metres", "metric")));
        when(conversionResultRepository.save(any(ConversionResultEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ConversionResultResponse response = conversionService.convert(
                new ConversionRequest(new BigDecimal("32.8084"), "feet", "metres"));

        assertThat(response.sourceUnit()).isEqualTo("feet");
        assertThat(response.targetUnit()).isEqualTo("metres");
        verify(conversionResultRepository, times(1)).save(any(ConversionResultEntity.class));
    }

    @Test
    void should_throwIncompatibleUnitsException_when_crossGroupUnitsProvided() {
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(unit("metres", "metric")));
        when(unitRepository.findByName("gallons")).thenReturn(Optional.of(unit("gallons", "imperial")));

        assertThatThrownBy(() -> conversionService.convert(
                new ConversionRequest(new BigDecimal("10"), "metres", "gallons")))
                .isInstanceOf(IncompatibleUnitsException.class)
                .hasMessage("Incompatible units: metres and gallons");
    }

    @Test
    void should_throwUnsupportedUnitException_when_unknownSourceUnitProvided() {
        when(unitRepository.findByName("parsecs")).thenReturn(Optional.empty());
        when(unitRepository.findByName("feet")).thenReturn(Optional.of(unit("feet", "imperial")));

        assertThatThrownBy(() -> conversionService.convert(
                new ConversionRequest(new BigDecimal("10"), "parsecs", "feet")))
                .isInstanceOf(UnsupportedUnitException.class)
                .hasMessage("Unsupported unit: parsecs");
    }

    @Test
    void should_returnSupportedUnits_when_listUnits() {
        when(unitRepository.findAllByOrderByNameAsc()).thenReturn(List.of(
                unit("feet", "imperial"),
                unit("gallons", "imperial"),
                unit("kilometres", "metric"),
                unit("litres", "metric"),
                unit("metres", "metric"),
                unit("miles", "imperial")));

        List<UnitResponse> units = conversionService.listUnits();

        assertThat(units).hasSize(6);
        assertThat(units).extracting(UnitResponse::name)
                .containsExactly("feet", "gallons", "kilometres", "litres", "metres", "miles");
    }

    @Test
    void should_persistExactlyOneConversionResult_when_convertCalled() {
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(unit("metres", "metric")));
        when(unitRepository.findByName("feet")).thenReturn(Optional.of(unit("feet", "imperial")));
        when(conversionResultRepository.save(any(ConversionResultEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        conversionService.convert(new ConversionRequest(new BigDecimal("10"), "metres", "feet"));

        ArgumentCaptor<ConversionResultEntity> captor = ArgumentCaptor.forClass(ConversionResultEntity.class);
        verify(conversionResultRepository).save(captor.capture());
        assertThat(captor.getValue().getSourceUnit()).isEqualTo("metres");
        assertThat(captor.getValue().getTargetUnit()).isEqualTo("feet");
    }
}
