package com.edgarkirk.unitconv.service;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.api.dto.response.UnitResponse;
import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.entity.Unit;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitException;
import com.edgarkirk.unitconv.service.exception.UnsupportedUnitException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversionServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ConversionResultRepository conversionResultRepository;

    @InjectMocks
    private ConversionService conversionService;

    @Test
    void should_returnPersistedConversionResult_when_convertMetresToFeet() {
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "metres", "metric")));
        when(unitRepository.findByName("feet")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "feet", "imperial")));
        when(conversionResultRepository.save(any())).thenAnswer(invocation -> {
            ConversionResult conversionResult = invocation.getArgument(0);
            return new ConversionResult(
                    UUID.fromString("11111111-1111-1111-1111-111111111111"),
                    conversionResult.getInputValue(),
                    conversionResult.getSourceUnit(),
                    conversionResult.getTargetUnit(),
                    conversionResult.getResult());
        });

        ConversionResultResponse response = conversionService.convert(new ConversionRequest(BigDecimal.valueOf(12.5), "metres", "feet"));

        assertThat(response.id()).isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        assertThat(response.inputValue()).isEqualByComparingTo("12.5");
        assertThat(response.sourceUnit()).isEqualTo("metres");
        assertThat(response.targetUnit()).isEqualTo("feet");
        assertThat(response.result()).isEqualByComparingTo("41.010499");
        verify(conversionResultRepository).save(any());
    }

    @Test
    void should_failWhenPersistenceDoesNotReturnSavedConversionResult() {
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "metres", "metric")));
        when(unitRepository.findByName("feet")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "feet", "imperial")));
        when(conversionResultRepository.save(any())).thenReturn(null);

        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(BigDecimal.valueOf(12.5), "metres", "feet")))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_returnConvertedValue_when_convertKilometresToMiles() {
        when(unitRepository.findByName("kilometres")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "kilometres", "metric")));
        when(unitRepository.findByName("miles")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "miles", "imperial")));
        when(conversionResultRepository.save(any())).thenAnswer(invocation -> {
            ConversionResult conversionResult = invocation.getArgument(0);
            return new ConversionResult(
                    UUID.fromString("22222222-2222-2222-2222-222222222222"),
                    conversionResult.getInputValue(),
                    conversionResult.getSourceUnit(),
                    conversionResult.getTargetUnit(),
                    conversionResult.getResult());
        });

        ConversionResultResponse response = conversionService.convert(new ConversionRequest(BigDecimal.valueOf(10), "kilometres", "miles"));

        assertThat(response.result()).isEqualByComparingTo("6.213712");
    }

    @Test
    void should_returnConvertedValue_when_convertLitresToGallons() {
        when(unitRepository.findByName("litres")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "litres", "metric")));
        when(unitRepository.findByName("gallons")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "gallons", "imperial")));
        when(conversionResultRepository.save(any())).thenAnswer(invocation -> {
            ConversionResult conversionResult = invocation.getArgument(0);
            return new ConversionResult(
                    UUID.fromString("33333333-3333-3333-3333-333333333333"),
                    conversionResult.getInputValue(),
                    conversionResult.getSourceUnit(),
                    conversionResult.getTargetUnit(),
                    conversionResult.getResult());
        });

        ConversionResultResponse response = conversionService.convert(new ConversionRequest(BigDecimal.valueOf(3), "litres", "gallons"));

        assertThat(response.result()).isEqualByComparingTo("0.792516");
    }

    @Test
    void should_throwIncompatibleUnitException_when_convertMetresToGallons() {
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "metres", "metric")));
        when(unitRepository.findByName("gallons")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "gallons", "imperial")));

        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(BigDecimal.ONE, "metres", "gallons")))
                .isInstanceOf(IncompatibleUnitException.class)
                .hasMessage("Incompatible units: metres to gallons");
    }

    @Test
    void should_throwUnsupportedUnitException_when_convertUnsupportedUnit() {
        when(unitRepository.findByName("parsecs")).thenReturn(Optional.empty());
        when(unitRepository.findByName("feet")).thenReturn(Optional.of(new Unit(UUID.randomUUID(), "feet", "imperial")));

        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(BigDecimal.ONE, "parsecs", "feet")))
                .isInstanceOf(UnsupportedUnitException.class)
                .hasMessage("Unsupported unit: parsecs");
    }

    @Test
    void should_returnSupportedUnits_when_listUnits() {
        when(unitRepository.findAll()).thenReturn(List.of(
                new Unit(UUID.randomUUID(), "metres", "metric"),
                new Unit(UUID.randomUUID(), "feet", "imperial")));

        List<UnitResponse> units = conversionService.listUnits();

        assertThat(units).hasSize(2);
        verify(unitRepository).findAll();
    }
}
