package com.edgarkirk.unitconv.api;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.edgarkirk.unitconv.api.dto.response.ConversionResult;
import com.edgarkirk.unitconv.service.ConversionService;
import com.edgarkirk.unitconv.service.exception.IncompatibleUnitsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ConversionController.class)
@ActiveProfiles("test")
class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ConversionService conversionService;

    @Test
    void should_return200_when_valid_conversion_request_is_submitted() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        ConversionResult response = new ConversionResult(id, new BigDecimal("12.5"), "metres", "feet", new BigDecimal("41.0105"));
        org.mockito.Mockito.when(conversionService.convert(org.mockito.ArgumentMatchers.any())).thenReturn(response);
        ConversionRequest request = new ConversionRequest(new BigDecimal("12.5"), "metres", "feet");

        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.inputValue").value(12.5))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andExpect(jsonPath("$.result").value(41.0105));
    }

    @Test
    void should_return400_when_service_reports_incompatible_units() throws Exception {
        // Arrange
        org.mockito.Mockito.when(conversionService.convert(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new IncompatibleUnitsException("Incompatible units: metres cannot be converted to gallons"));
        ConversionRequest request = new ConversionRequest(new BigDecimal("1"), "metres", "gallons");

        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").value(containsString("incompatible units")));
    }

    @Test
    void should_return400_when_value_is_not_numeric() throws Exception {
        // Arrange
        String requestBody = """
                {"value":"abc","sourceUnit":"metres","targetUnit":"feet"}
                """;

        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("invalid numeric value")))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400_when_value_is_missing_from_request() throws Exception {
        // Arrange
        String requestBody = """
                {"sourceUnit":"metres","targetUnit":"feet"}
                """;

        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
