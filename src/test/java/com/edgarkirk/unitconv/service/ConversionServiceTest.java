package com.edgarkirk.unitconv.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.dto.response.UnitResponse;
import com.edgarkirk.unitconv.persistence.entity.ConversionResult;
import com.edgarkirk.unitconv.persistence.entity.Unit;
import com.edgarkirk.unitconv.service.dao.ConversionResultDao;
import com.edgarkirk.unitconv.service.dao.UnitDao;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitsException;
import com.edgarkirk.unitconv.service.exception.UnknownUnitException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConversionServiceTest {

    @Mock
    private UnitDao unitDao;

    @Mock
    private ConversionResultDao conversionResultDao;

    @InjectMocks
    private ConversionServiceImpl conversionService;

    @Test
    void should_persistAndReturnConvertedResult_when_lengthConversionIsValid() {
        // Arrange
        ConversionRequest request = new ConversionRequest(new BigDecimal("10"), "metres", "feet");
        Unit metres = new Unit(UUID.randomUUID(), "metres", "metric");
        Unit feet = new Unit(UUID.randomUUID(), "feet", "imperial");
        when(unitDao.findByName("metres")).thenReturn(Optional.of(metres));
        when(unitDao.findByName("feet")).thenReturn(Optional.of(feet));
        when(conversionResultDao.save(org.mockito.ArgumentMatchers.any(ConversionResult.class)))
                .thenAnswer(invocation -> {
                    ConversionResult argument = invocation.getArgument(0);
                    return new ConversionResult(
                            UUID.fromString("00000000-0000-0000-0000-000000000001"),
                            argument.getInputValue(),
                            argument.getSourceUnit(),
                            argument.getTargetUnit(),
                            argument.getResult());
                });

        // Act
        ConversionResultResponse actual = conversionService.convert(request);

        // Assert
        assertThat(actual.id()).isNotNull();
        assertThat(actual.inputValue()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(actual.sourceUnit()).isEqualTo("metres");
        assertThat(actual.targetUnit()).isEqualTo("feet");
        assertThat(actual.result()).isEqualByComparingTo(new BigDecimal("32.808400"));
        verify(conversionResultDao).save(org.mockito.ArgumentMatchers.any(ConversionResult.class));
    }

    @Test
    void should_returnOriginalValue_when_sourceAndTargetUnitAreTheSame() {
        // Arrange
        ConversionRequest request = new ConversionRequest(new BigDecimal("10"), "metres", "metres");
        Unit metres = new Unit(UUID.randomUUID(), "metres", "metric");
        when(unitDao.findByName("metres")).thenReturn(Optional.of(metres));
        when(conversionResultDao.save(org.mockito.ArgumentMatchers.any(ConversionResult.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ConversionResultResponse actual = conversionService.convert(request);

        // Assert
        assertThat(actual.result()).isEqualByComparingTo(new BigDecimal("10.000000"));
    }

    @Test
    void should_throwIncompatibleUnitsException_when_unitsSpanDifferentGroups() {
        // Arrange
        ConversionRequest request = new ConversionRequest(new BigDecimal("10"), "metres", "gallons");
        Unit metres = new Unit(UUID.randomUUID(), "metres", "metric");
        Unit gallons = new Unit(UUID.randomUUID(), "gallons", "imperial");
        when(unitDao.findByName("metres")).thenReturn(Optional.of(metres));
        when(unitDao.findByName("gallons")).thenReturn(Optional.of(gallons));

        // Act / Assert
        assertThatThrownBy(() -> conversionService.convert(request))
                .isInstanceOf(IncompatibleUnitsException.class)
                .hasMessage("Incompatible units: metres and gallons");
    }

    @Test
    void should_throwUnknownUnitException_when_sourceUnitDoesNotExist() {
        // Arrange
        ConversionRequest request = new ConversionRequest(new BigDecimal("10"), "yards", "feet");
        when(unitDao.findByName("yards")).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> conversionService.convert(request))
                .isInstanceOf(UnknownUnitException.class)
                .hasMessage("Unknown unit: yards");
    }

    @Test
    void should_returnSupportedUnits_when_unitsAreRequested() {
        // Arrange
        List<Unit> units = List.of(
                new Unit(UUID.randomUUID(), "metres", "metric"),
                new Unit(UUID.randomUUID(), "feet", "imperial"));
        when(unitDao.findAllSupportedUnits()).thenReturn(units);

        // Act
        List<UnitResponse> actual = conversionService.listSupportedUnits();

        // Assert
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0).name()).isEqualTo("metres");
    }
}
