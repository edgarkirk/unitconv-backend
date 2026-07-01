package com.edgarkirk.unitconv.api;

import static org.hamcrest.Matchers.closeTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import com.edgarkirk.unitconv.api.response.ConversionResult;
import com.edgarkirk.unitconv.application.ConversionService;
import com.edgarkirk.unitconv.application.exception.IncompatibleUnitsException;
import com.edgarkirk.unitconv.application.exception.UnsupportedUnitException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ConversionController.class)
@Import(com.edgarkirk.unitconv.api.advice.GlobalExceptionHandler.class)
class ConversionControllerTest {

    private static final UUID CONVERSION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversionService conversionService;

    private ConversionResult conversionResult;

    @BeforeEach
    void set_up() {
        conversionResult = new ConversionResult(
                CONVERSION_ID,
                new BigDecimal("1"),
                "metres",
                "feet",
                new BigDecimal("3.28084"));
    }

    @Test
    void converts_measurement_successfully() throws Exception {
        // Arrange
        given(conversionService.convert(org.mockito.ArgumentMatchers.any())).willReturn(conversionResult);

        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":1,"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CONVERSION_ID.toString()))
                .andExpect(jsonPath("$.inputValue").value(1))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andExpect(jsonPath("$.result").value(closeTo(3.28084, 0.000001)));
    }

    @Test
    void rejects_incompatible_units_with_validation_error() throws Exception {
        // Arrange
        given(conversionService.convert(org.mockito.ArgumentMatchers.any()))
                .willThrow(new IncompatibleUnitsException("metres", "gallons"));

        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":1,"sourceUnit":"metres","targetUnit":"gallons"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Conversion between 'metres' and 'gallons' is not supported. Supported pairs are metres/feet, kilometres/miles, and litres/gallons."))
                .andExpect(jsonPath("$.field").doesNotExist());
    }

    @Test
    void rejects_unknown_source_unit_with_validation_error() throws Exception {
        // Arrange
        given(conversionService.convert(org.mockito.ArgumentMatchers.any()))
                .willThrow(new UnsupportedUnitException("sourceUnit", "stones"));

        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":1,"sourceUnit":"stones","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Unsupported sourceUnit 'stones'. Supported units are metres, feet, kilometres, miles, litres, and gallons."))
                .andExpect(jsonPath("$.field").value("sourceUnit"));
    }

    @Test
    void rejects_unknown_target_unit_with_validation_error() throws Exception {
        // Arrange
        given(conversionService.convert(org.mockito.ArgumentMatchers.any()))
                .willThrow(new UnsupportedUnitException("targetUnit", "barrels"));

        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":1,"sourceUnit":"feet","targetUnit":"barrels"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Unsupported targetUnit 'barrels'. Supported units are metres, feet, kilometres, miles, litres, and gallons."))
                .andExpect(jsonPath("$.field").value("targetUnit"));
    }

    @Test
    void rejects_non_numeric_value_with_validation_error() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":"abc","sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Request field 'value' must be numeric."))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void rejects_missing_value_with_validation_error() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Request field 'value' is required."))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void rejects_missing_source_unit_with_validation_error() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":1,"targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Request field 'sourceUnit' is required."))
                .andExpect(jsonPath("$.field").value("sourceUnit"));
    }

    @Test
    void rejects_missing_target_unit_with_validation_error() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":1,"sourceUnit":"metres"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Request field 'targetUnit' is required."))
                .andExpect(jsonPath("$.field").value("targetUnit"));
    }
}
