package com.edgarkirk.unitconv.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.edgarkirk.unitconv.application.exception.ConversionValidationException;
import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResult;
import com.edgarkirk.unitconv.persistence.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.UnitRepository;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ConversionServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ConversionResultRepository conversionResultRepository;

    @InjectMocks
    private ConversionServiceImpl conversionService;

    @Captor
    private ArgumentCaptor<com.edgarkirk.unitconv.persistence.ConversionResult> conversionResultCaptor;

    private com.edgarkirk.unitconv.persistence.Unit metres;
    private com.edgarkirk.unitconv.persistence.Unit feet;
    private com.edgarkirk.unitconv.persistence.Unit gallons;

    @BeforeEach
    void setUp() {
        metres = unit("11111111-1111-1111-1111-111111111111", "metres", "metric");
        feet = unit("22222222-2222-2222-2222-222222222222", "feet", "imperial");
        gallons = unit("66666666-6666-6666-6666-666666666666", "gallons", "imperial");
    }

    @Test
    void should_convertMetresToFeet_and_saveResult_when_unitsAreCompatible() {
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(metres));
        when(unitRepository.findByName("feet")).thenReturn(Optional.of(feet));
        when(conversionResultRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        final UUID requestId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        final ConversionResult result = conversionService.convert(
                new ConversionRequest(requestId, new BigDecimal("1"), "metres", "feet"));

        assertThat(result.id()).isEqualTo(requestId);
        assertThat(result.inputValue()).isEqualByComparingTo("1");
        assertThat(result.sourceUnit()).isEqualTo("metres");
        assertThat(result.targetUnit()).isEqualTo("feet");
        assertThat(result.result()).isEqualByComparingTo("3.280840");

        verify(conversionResultRepository).save(conversionResultCaptor.capture());
        assertThat(conversionResultCaptor.getValue().getInputValue()).isEqualByComparingTo("1");
        assertThat(conversionResultCaptor.getValue().getSourceUnit()).isEqualTo("metres");
        assertThat(conversionResultCaptor.getValue().getTargetUnit()).isEqualTo("feet");
        assertThat(conversionResultCaptor.getValue().getResult()).isEqualByComparingTo("3.280840");
    }

    @Test
    void should_convertFeetToMetres_and_roundToSixDecimals_when_unitsAreCompatible() {
        when(unitRepository.findByName("feet")).thenReturn(Optional.of(feet));
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(metres));
        when(conversionResultRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        final ConversionResult result = conversionService.convert(
                new ConversionRequest(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"), new BigDecimal("3.28084"), "feet", "metres"));

        assertThat(result.result()).isEqualByComparingTo("1.000000");
    }

    @Test
    void should_throwValidationException_when_unitsAreIncompatible() {
        when(unitRepository.findByName("metres")).thenReturn(Optional.of(metres));
        when(unitRepository.findByName("gallons")).thenReturn(Optional.of(gallons));

        assertThatThrownBy(() -> conversionService.convert(
                new ConversionRequest(UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"), new BigDecimal("1"), "metres", "gallons")))
                .isInstanceOf(ConversionValidationException.class)
                .hasMessage("Units 'metres' and 'gallons' are incompatible.");
    }

    @Test
    void should_throwValidationException_when_sourceUnitIsNotSupported() {
        when(unitRepository.findByName("furlongs")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conversionService.convert(
                new ConversionRequest(UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"), new BigDecimal("1"), "furlongs", "feet")))
                .isInstanceOf(ConversionValidationException.class)
                .hasMessage("Source unit 'furlongs' is not supported.");
    }

    @Test
    void should_returnUnits_when_repositoryContainsSupportedUnits() {
        when(unitRepository.findAll()).thenReturn(List.of(metres, feet));

        final List<com.edgarkirk.unitconv.dto.response.Unit> units = conversionService.listUnits();

        assertThat(units).hasSize(2);
        assertThat(units.get(0).id()).isEqualTo(metres.getId());
        assertThat(units.get(0).name()).isEqualTo("metres");
        assertThat(units.get(0).system()).isEqualTo("metric");
    }

    private com.edgarkirk.unitconv.persistence.Unit unit(final String id, final String name, final String system) {
        final com.edgarkirk.unitconv.persistence.Unit unit = new com.edgarkirk.unitconv.persistence.Unit(name, system);
        ReflectionTestUtils.setField(unit, "id", UUID.fromString(id));
        return unit;
    }
}
