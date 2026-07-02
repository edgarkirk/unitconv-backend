package com.edgarkirk.unitconv.service;

import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.dto.response.UnitResponse;
import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.entity.Unit;
import com.edgarkirk.unitconv.service.dao.ConversionResultDao;
import com.edgarkirk.unitconv.service.dao.UnitDao;
import com.edgarkirk.unitconv.service.exception.ConversionValidationException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversionServiceTest {

    @Mock
    private UnitDao unitDao;

    @Mock
    private ConversionResultDao conversionResultDao;

    @InjectMocks
    private ConversionService conversionService;

    @Test
    void should_returnConvertedResult_when_validLengthConversion() {
        Unit metres = unit("metres", "metric");
        Unit feet = unit("feet", "imperial");
        ConversionRequest request = new ConversionRequest(new BigDecimal("10"), "metres", "feet");

        when(unitDao.findByName("metres")).thenReturn(Optional.of(metres));
        when(unitDao.findByName("feet")).thenReturn(Optional.of(feet));

        ConversionResult persisted = new ConversionResult();
        persisted.setId(UUID.randomUUID());
        persisted.setInputValue(new BigDecimal("10"));
        persisted.setSourceUnit("metres");
        persisted.setTargetUnit("feet");
        persisted.setResult(new BigDecimal("32.808400"));
        when(conversionResultDao.save(org.mockito.ArgumentMatchers.any(ConversionResult.class))).thenReturn(persisted);

        ConversionResultResponse response = conversionService.convert(request);

        assertThat(response.id()).isEqualTo(persisted.getId());
        assertThat(response.inputValue()).isEqualByComparingTo("10");
        assertThat(response.sourceUnit()).isEqualTo("metres");
        assertThat(response.targetUnit()).isEqualTo("feet");
        assertThat(response.result()).isEqualByComparingTo("32.808400");
        verify(conversionResultDao).save(org.mockito.ArgumentMatchers.any(ConversionResult.class));
    }

    @Test
    void should_returnReversibleResult_when_convertingMetresToFeetAndBack() {
        Unit metres = unit("metres", "metric");
        Unit feet = unit("feet", "imperial");
        when(unitDao.findByName("metres")).thenReturn(Optional.of(metres));
        when(unitDao.findByName("feet")).thenReturn(Optional.of(feet));
        when(conversionResultDao.save(org.mockito.ArgumentMatchers.any(ConversionResult.class)))
                .thenAnswer(invocation -> {
                    ConversionResult input = invocation.getArgument(0);
                    input.setId(UUID.randomUUID());
                    return input;
                });

        ConversionResultResponse first = conversionService.convert(new ConversionRequest(new BigDecimal("10"), "metres", "feet"));
        ConversionResultResponse second = conversionService.convert(new ConversionRequest(first.result(), "feet", "metres"));

        assertThat(second.result().subtract(new BigDecimal("10")).abs())
                .isLessThanOrEqualTo(new BigDecimal("0.000001"));
    }

    @Test
    void should_throwValidationException_when_unitsAreIncompatible() {
        Unit metres = unit("metres", "metric");
        Unit gallons = unit("gallons", "imperial");
        when(unitDao.findByName("metres")).thenReturn(Optional.of(metres));
        when(unitDao.findByName("gallons")).thenReturn(Optional.of(gallons));

        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(new BigDecimal("10"), "metres", "gallons")))
                .isInstanceOf(ConversionValidationException.class)
                .hasMessage("Incompatible units: metres and gallons");
    }

    @Test
    void should_throwValidationException_when_sourceUnitIsUnsupported() {
        when(unitDao.findByName("parsec")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(new BigDecimal("10"), "parsec", "feet")))
                .isInstanceOf(ConversionValidationException.class)
                .hasMessage("Unsupported unit: parsec");
    }

    @Test
    void should_throwValidationException_when_targetUnitIsUnsupported() {
        Unit metres = unit("metres", "metric");
        when(unitDao.findByName("metres")).thenReturn(Optional.of(metres));
        when(unitDao.findByName("parsec")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(new BigDecimal("10"), "metres", "parsec")))
                .isInstanceOf(ConversionValidationException.class)
                .hasMessage("Unsupported unit: parsec");
    }

    @Test
    void should_returnAllUnits_when_listingUnits() {
        when(unitDao.findAllByOrderByNameAsc()).thenReturn(List.of(unit("feet", "imperial")));

        List<UnitResponse> units = conversionService.getUnits();

        assertThat(units).hasSize(1);
        assertThat(units.get(0).name()).isEqualTo("feet");
    }

    private Unit unit(String name, String system) {
        Unit unit = new Unit();
        unit.setId(UUID.randomUUID());
        unit.setName(name);
        unit.setSystem(system);
        return unit;
    }
}
