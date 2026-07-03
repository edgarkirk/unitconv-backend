package com.edgarkirk.unitconv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import com.edgarkirk.unitconv.api.dto.request.ConversionRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UnitconvAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_return200_when_converting_metres_to_feet() throws Exception {
        // Arrange
        ConversionRequest request = new ConversionRequest(new BigDecimal("12.5"), "metres", "feet");

        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.inputValue").value(12.5))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andExpect(jsonPath("$.result").isNumber());
    }

    @Test
    void should_return200_when_reverse_converting_feet_to_metres_within_precision() throws Exception {
        // Arrange
        ConversionRequest firstRequest = new ConversionRequest(new BigDecimal("12.5"), "metres", "feet");

        // Act
        String firstBody = mockMvc.perform(post("/api/convert")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode firstJson = objectMapper.readTree(firstBody);
        BigDecimal feetValue = new BigDecimal(firstJson.get("result").asText());

        ConversionRequest secondRequest = new ConversionRequest(feetValue, "feet", "metres");

        // Assert
        String secondBody = mockMvc.perform(post("/api/convert")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inputValue").value(feetValue))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode secondJson = objectMapper.readTree(secondBody);
        assertThat(new BigDecimal(secondJson.get("result").asText())).isEqualByComparingTo(new BigDecimal("12.5"));
    }

    @Test
    void should_return400_when_converting_incompatible_units() throws Exception {
        // Arrange
        String requestBody = """
                {"value":1,"sourceUnit":"metres","targetUnit":"gallons"}
                """;

        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").value(containsString("incompatible units")));
    }

    @Test
    void should_return400_when_value_is_non_numeric() throws Exception {
        // Arrange
        String requestBody = """
                {"value":"abc","sourceUnit":"metres","targetUnit":"feet"}
                """;

        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").value(containsString("invalid numeric value")))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400_when_value_is_missing() throws Exception {
        // Arrange
        String requestBody = """
                {"sourceUnit":"metres","targetUnit":"feet"}
                """;

        // Act & Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void should_return200_when_listing_supported_units() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[*].name").value(containsInAnyOrder("metres", "feet", "kilometres", "miles", "litres", "gallons")))
                .andExpect(jsonPath("$[*].system").value(containsInAnyOrder("metric", "imperial", "metric", "imperial", "metric", "imperial")));
    }

    @Test
    void should_return404_when_batch_conversion_endpoint_is_called() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/convert/batch")
                        .contentType(APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return405_when_attempting_to_create_units() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/units"))
                .andExpect(status().isMethodNotAllowed());
    }
}
