package com.edgarkirk.unitconv.service;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResult;
import com.edgarkirk.unitconv.api.dto.response.Unit;
import com.edgarkirk.unitconv.service.dao.ConversionResultDao;
import com.edgarkirk.unitconv.service.dao.UnitDao;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitsException;
import com.edgarkirk.unitconv.service.exception.InvalidConversionException;
import com.edgarkirk.unitconv.service.exception.UnsupportedUnitException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversionServiceTest {

    @Mock
    private UnitDao unitDao;

    @Mock
    private ConversionResultDao conversionResultDao;

    @InjectMocks
    private ConversionServiceImpl conversionService;

    @Test
    void should_convertMetresToFeet_when_unitsAreCompatible() {
        when(unitDao.findAll()).thenReturn(List.of(
                new com.edgarkirk.unitconv.persistence.entity.Unit("metres", "metric"),
                new com.edgarkirk.unitconv.persistence.entity.Unit("feet", "imperial")));
        when(conversionResultDao.save(any(com.edgarkirk.unitconv.persistence.entity.ConversionResult.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ConversionResult converted = conversionService.convert(new ConversionRequest(new BigDecimal("10"), "metres", "feet"));

        assertThat(converted.sourceUnit()).isEqualTo("metres");
        assertThat(converted.result()).isEqualByComparingTo("32.808400");
    }

    @Test
    void should_returnReverseConversion_when_convertingFeetToMetres() {
        when(unitDao.findAll()).thenReturn(List.of(
                new com.edgarkirk.unitconv.persistence.entity.Unit("feet", "imperial"),
                new com.edgarkirk.unitconv.persistence.entity.Unit("metres", "metric")));
        when(conversionResultDao.save(any(com.edgarkirk.unitconv.persistence.entity.ConversionResult.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ConversionResult converted = conversionService.convert(new ConversionRequest(new BigDecimal("3.28084"), "feet", "metres"));

        assertThat(converted.result()).isEqualByComparingTo("1.000000");
    }

    @Test
    void should_rejectIncompatibleUnits_when_unitsAreDifferentGroups() {
        when(unitDao.findAll()).thenReturn(List.of(
                new com.edgarkirk.unitconv.persistence.entity.Unit("metres", "metric"),
                new com.edgarkirk.unitconv.persistence.entity.Unit("gallons", "imperial")));

        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(new BigDecimal("10"), "metres", "gallons")))
                .isInstanceOf(IncompatibleUnitsException.class)
                .hasMessage("Incompatible units: metres cannot be converted to gallons");
    }

    @Test
    void should_rejectUnsupportedUnit_when_sourceUnitIsMissingFromLookup() {
        when(unitDao.findAll()).thenReturn(List.of(
                new com.edgarkirk.unitconv.persistence.entity.Unit("metres", "metric"),
                new com.edgarkirk.unitconv.persistence.entity.Unit("feet", "imperial")));
        when(unitDao.findByName("yards")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(new BigDecimal("10"), "yards", "feet")))
                .isInstanceOf(UnsupportedUnitException.class)
                .hasMessage("Unsupported unit: yards");
    }

    @Test
    void should_rejectMissingValue_when_requestValueIsNull() {
        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(null, "metres", "feet")))
                .isInstanceOf(InvalidConversionException.class)
                .hasMessage("Missing required field: value");
    }

    @Test
    void should_returnAllUnits_when_listingSupportedUnits() {
        when(unitDao.findAll()).thenReturn(List.of(
                new com.edgarkirk.unitconv.persistence.entity.Unit("metres", "metric"),
                new com.edgarkirk.unitconv.persistence.entity.Unit("feet", "imperial")));

        List<Unit> units = conversionService.listUnits();

        assertThat(units).hasSize(2);
    }
}
