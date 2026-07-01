package com.edgarkirk.unitconv.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.edgarkirk.unitconv.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.dto.response.ConversionResult;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ConversionServiceTest {

    private final UnitCatalog unitCatalog = new InMemoryUnitCatalog();

    @Test
    void should_convertMetresToFeet_when_validRequest() {
        DefaultConversionService service = new DefaultConversionService(unitCatalog);
        ConversionRequest request = new ConversionRequest(
                UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"),
                new BigDecimal("1"),
                "metres",
                "feet");

        ConversionResult result = service.convert(request);

        assertThat(result.id()).isEqualTo(request.id());
        assertThat(result.inputValue()).isEqualByComparingTo("1");
        assertThat(result.sourceUnit()).isEqualTo("metres");
        assertThat(result.targetUnit()).isEqualTo("feet");
        assertThat(result.result()).isEqualByComparingTo("3.28084");
    }

    @Test
    void should_convertFeetToMetres_when_reverseConversion() {
        DefaultConversionService service = new DefaultConversionService(unitCatalog);
        ConversionRequest request = new ConversionRequest(
                UUID.fromString("bbbbbbbb-cccc-dddd-eeee-ffffffffffff"),
                new BigDecimal("3.28084"),
                "feet",
                "metres");

        ConversionResult result = service.convert(request);

        assertThat(result.result()).isCloseTo(
                new BigDecimal("1.0"), org.assertj.core.data.Offset.offset(new BigDecimal("0.00001")));
    }

    @Test
    void should_rejectIncompatibleUnits_when_sourceAndTargetDifferInCompatibilityGroup() {
        DefaultConversionService service = new DefaultConversionService(unitCatalog);
        ConversionRequest request = new ConversionRequest(
                UUID.fromString("cccccccc-dddd-eeee-ffff-000000000000"),
                new BigDecimal("1"),
                "metres",
                "gallons");

        assertThatThrownBy(() -> service.convert(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible units: metres to gallons");
    }
}
