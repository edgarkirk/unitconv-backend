package com.edgarkirk.unitconv.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.edgarkirk.unitconv.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.dto.response.UnitResponse;
import com.edgarkirk.unitconv.service.ConversionService;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitsException;
import com.edgarkirk.unitconv.service.exception.UnknownUnitException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ConversionController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversionService conversionService;

    @Test
    void should_return200_and_conversionResult_when_validRequestIsSubmitted() throws Exception {
        // Arrange
        ConversionResultResponse response = new ConversionResultResponse(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                new BigDecimal("10"),
                "metres",
                "feet",
                new BigDecimal("32.8084"));
        when(conversionService.convert(org.mockito.ArgumentMatchers.any())).thenReturn(response);

        // Act / Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "value": 10,
                                  "sourceUnit": "metres",
                                  "targetUnit": "feet"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("00000000-0000-0000-0000-000000000001"))
                .andExpect(jsonPath("$.inputValue").value(10))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andExpect(jsonPath("$.result").value(32.8084));
    }

    @Test
    void should_return400_and_validationError_when_units_are_incompatible() throws Exception {
        // Arrange
        when(conversionService.convert(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new IncompatibleUnitsException("Incompatible units: metres and gallons"));

        // Act / Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "value": 10,
                                  "sourceUnit": "metres",
                                  "targetUnit": "gallons"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.message").value("Incompatible units: metres and gallons"))
                .andExpect(jsonPath("$.field").doesNotExist());
    }

    @Test
    void should_return400_and_validationError_when_value_is_non_numeric() throws Exception {
        // Arrange

        // Act / Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "value": "abc",
                                  "sourceUnit": "metres",
                                  "targetUnit": "feet"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.message").value("Invalid value: expected a numeric JSON number"))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400_and_validationError_when_value_is_missing() throws Exception {
        // Arrange

        // Act / Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sourceUnit": "metres",
                                  "targetUnit": "feet"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.message").value("Missing required field: value"))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400_and_validationError_when_sourceUnit_is_missing() throws Exception {
        // Act / Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "value": 10,
                                  "targetUnit": "feet"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.message").value("Missing required field: sourceUnit"))
                .andExpect(jsonPath("$.field").value("sourceUnit"));
    }

    @Test
    void should_return400_and_validationError_when_targetUnit_is_missing() throws Exception {
        // Act / Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "value": 10,
                                  "sourceUnit": "metres"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.message").value("Missing required field: targetUnit"))
                .andExpect(jsonPath("$.field").value("targetUnit"));
    }

    @Test
    void should_return400_and_validationError_when_unknownUnit_is_requested() throws Exception {
        // Arrange
        when(conversionService.convert(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new UnknownUnitException("Unknown unit: yards", "sourceUnit"));

        // Act / Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "value": 10,
                                  "sourceUnit": "yards",
                                  "targetUnit": "feet"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.message").value("Unknown unit: yards"))
                .andExpect(jsonPath("$.field").value("sourceUnit"));
    }

    @Test
    void should_return200_and_supportedUnits_when_getUnits_isInvoked() throws Exception {
        // Arrange
        when(conversionService.listSupportedUnits()).thenReturn(List.of(
                new UnitResponse(UUID.fromString("00000000-0000-0000-0000-000000000001"), "metres", "metric"),
                new UnitResponse(UUID.fromString("00000000-0000-0000-0000-000000000002"), "feet", "imperial")));

        // Act / Assert
        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("metres"))
                .andExpect(jsonPath("$[0].system").value("metric"))
                .andExpect(jsonPath("$[1].name").value("feet"))
                .andExpect(jsonPath("$[1].system").value("imperial"));
    }

    @Test
    void should_return404_when_batchEndpoint_is_requested() throws Exception {
        // Arrange

        // Act / Assert
        mockMvc.perform(post("/api/convert/batch"))
                .andExpect(status().isNotFound());
    }
}
