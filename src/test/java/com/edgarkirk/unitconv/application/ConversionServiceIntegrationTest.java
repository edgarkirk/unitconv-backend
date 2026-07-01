package com.edgarkirk.unitconv.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;

import com.edgarkirk.unitconv.api.request.ConversionRequest;
import com.edgarkirk.unitconv.api.response.ConversionResult;
import com.edgarkirk.unitconv.api.response.Unit;
import com.edgarkirk.unitconv.application.exception.IncompatibleUnitsException;
import com.edgarkirk.unitconv.application.exception.UnsupportedUnitException;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(ConversionServiceImpl.class)
class ConversionServiceIntegrationTest {

    

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private ConversionResultRepository conversionResultRepository;

    private ConversionRequest metresToFeetRequest;

    @BeforeEach
    void set_up() {
        metresToFeetRequest = new ConversionRequest(new BigDecimal("1"), "metres", "feet");
    }

    @Test
    void converts_metres_to_feet_and_persists_result() {
        // Act
        ConversionResult result = conversionService.convert(metresToFeetRequest);

        // Assert
        assertThat(result.id()).isNotNull();
        assertThat(result.inputValue()).isEqualByComparingTo("1");
        assertThat(result.sourceUnit()).isEqualTo("metres");
        assertThat(result.targetUnit()).isEqualTo("feet");
        assertThat(result.result()).isEqualByComparingTo("3.28084");
        assertThat(conversionResultRepository.findAll())
                .singleElement()
                .satisfies(entity -> {
                    assertThat(entity.getId()).isEqualTo(result.id());
                    assertThat(entity.getInputValue()).isEqualByComparingTo("1");
                    assertThat(entity.getSourceUnit()).isEqualTo("metres");
                    assertThat(entity.getTargetUnit()).isEqualTo("feet");
                    assertThat(entity.getResult()).isEqualByComparingTo("3.28084");
                });
    }

    @Test
    void converts_feet_to_metres_back_to_original_value() {
        // Arrange
        ConversionResult forward = conversionService.convert(metresToFeetRequest);

        // Act
        ConversionResult reverse = conversionService.convert(new ConversionRequest(forward.result(), "feet", "metres"));

        // Assert
        assertThat(reverse.result()).isEqualByComparingTo("1");
    }

    @Test
    void lists_supported_units_from_database() {
        // Act
        List<Unit> units = conversionService.listSupportedUnits();

        // Assert
        assertThat(units).hasSize(6);
        assertThat(units).extracting(Unit::name)
                .containsExactly("feet", "gallons", "kilometres", "litres", "metres", "miles");
    }

    @Test
    void converts_identity_when_source_and_target_match() {
        // Act
        ConversionResult result = conversionService.convert(new ConversionRequest(new BigDecimal("7.5"), "metres", "metres"));

        // Assert
        assertThat(result.result()).isEqualByComparingTo("7.5");
    }

    @Test
    void rejects_incompatible_units_from_service_layer() {
        // Act & Assert
        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(new BigDecimal("1"), "metres", "gallons")))
                .isInstanceOf(IncompatibleUnitsException.class)
                .hasMessage("Conversion between 'metres' and 'gallons' is not supported. Supported pairs are metres/feet, kilometres/miles, and litres/gallons.");
    }

    @Test
    void rejects_unknown_source_unit_from_service_layer() {
        // Act & Assert
        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(new BigDecimal("1"), "stones", "feet")))
                .isInstanceOf(UnsupportedUnitException.class)
                .hasMessage("Unsupported sourceUnit 'stones'. Supported units are metres, feet, kilometres, miles, litres, and gallons.");
    }

    @Test
    void rejects_unknown_target_unit_from_service_layer() {
        // Act & Assert
        assertThatThrownBy(() -> conversionService.convert(new ConversionRequest(new BigDecimal("1"), "feet", "barrels")))
                .isInstanceOf(UnsupportedUnitException.class)
                .hasMessage("Unsupported targetUnit 'barrels'. Supported units are metres, feet, kilometres, miles, litres, and gallons.");
    }
}
