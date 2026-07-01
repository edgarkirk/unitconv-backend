package com.example.unitconv.application;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.unitconv.api.request.ConversionRequest;
import com.example.unitconv.api.response.ConversionResult;
import com.example.unitconv.api.response.UnitResponse;
import com.example.unitconv.application.exception.IncompatibleUnitsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConversionServiceTest {

    @Mock
    private UnitCatalog unitCatalog;

    @Test
    void should_convertMetresToFeet_when_validInput() {
        ConversionService service = new ConversionServiceImpl(unitCatalog);
        given(unitCatalog.findByName("metres")).willReturn(Optional.of(new UnitDefinition("metres", "metric", "length", BigDecimal.ONE)));
        given(unitCatalog.findByName("feet")).willReturn(Optional.of(new UnitDefinition("feet", "imperial", "length", new BigDecimal("3.28084"))));

        ConversionResult result = service.convert(new ConversionRequest(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"), new BigDecimal("1"), "metres", "feet"));

        assertThat(result.result().doubleValue(), closeTo(3.28084, 0.00001));
    }

    @Test
    void should_convertFeetToMetres_when_reverseInput() {
        ConversionService service = new ConversionServiceImpl(unitCatalog);
        given(unitCatalog.findByName("feet")).willReturn(Optional.of(new UnitDefinition("feet", "imperial", "length", new BigDecimal("3.28084"))));
        given(unitCatalog.findByName("metres")).willReturn(Optional.of(new UnitDefinition("metres", "metric", "length", BigDecimal.ONE)));

        ConversionResult result = service.convert(new ConversionRequest(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"), new BigDecimal("3.28084"), "feet", "metres"));

        assertThat(result.result().doubleValue(), closeTo(1.0, 0.00001));
    }

    @Test
    void should_convertKilometresToMiles_when_validInput() {
        ConversionService service = new ConversionServiceImpl(unitCatalog);
        given(unitCatalog.findByName("kilometres")).willReturn(Optional.of(new UnitDefinition("kilometres", "metric", "length", BigDecimal.ONE)));
        given(unitCatalog.findByName("miles")).willReturn(Optional.of(new UnitDefinition("miles", "imperial", "length", new BigDecimal("0.621371"))));

        ConversionResult result = service.convert(new ConversionRequest(UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"), new BigDecimal("1"), "kilometres", "miles"));

        assertThat(result.result().doubleValue(), closeTo(0.621371, 0.00001));
    }

    @Test
    void should_rejectCrossGroupConversion_when_unitsAreIncompatible() {
        ConversionService service = new ConversionServiceImpl(unitCatalog);
        given(unitCatalog.findByName("metres")).willReturn(Optional.of(new UnitDefinition("metres", "metric", "length", BigDecimal.ONE)));
        given(unitCatalog.findByName("gallons")).willReturn(Optional.of(new UnitDefinition("gallons", "imperial", "volume", new BigDecimal("0.264172"))));

        IncompatibleUnitsException exception = assertThrows(IncompatibleUnitsException.class,
            () -> service.convert(new ConversionRequest(UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"), new BigDecimal("1"), "metres", "gallons")));

        assertThat(exception.getMessage(), org.hamcrest.Matchers.is("Incompatible units: metres and gallons cannot be converted"));
    }

    @Test
    void should_listSupportedUnits_when_requested() {
        ConversionService service = new ConversionServiceImpl(unitCatalog);
        given(unitCatalog.allUnits()).willReturn(List.of(
            new UnitDefinition("metres", "metric", "length", BigDecimal.ONE),
            new UnitDefinition("feet", "imperial", "length", new BigDecimal("3.28084"))
        ));

        List<UnitResponse> units = service.units();

        assertThat(units, hasSize(2));
    }
}
