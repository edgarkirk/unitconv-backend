package com.edgarkirk.unitconv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UnitconvApplicationAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConversionResultRepository conversionResultRepository;

    @Test
    void should_return200_and_persistConversion_when_validMetresToFeetRequest() throws Exception {
        // Arrange
        String requestBody = """
                {
                  "value": 10,
                  "sourceUnit": "metres",
                  "targetUnit": "feet"
                }
                """;

        // Act
        String responseBody = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.inputValue").value(10))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andExpect(jsonPath("$.result").isNumber())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        // Assert
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        UUID id = UUID.fromString(jsonNode.get("id").asText());
        assertThat(conversionResultRepository.findById(id)).isPresent();
        assertThat(conversionResultRepository.findById(id).get().getId()).isEqualTo(id);
        assertThat(conversionResultRepository.findById(id).get().getInputValue()).isEqualByComparingTo(new BigDecimal("10"));
    }

    @Test
    void should_return200_and_restoreOriginalValue_when_reverseLengthConversionRequested() throws Exception {
        // Arrange
        String forwardRequest = """
                {
                  "value": 10,
                  "sourceUnit": "metres",
                  "targetUnit": "feet"
                }
                """;
        String forwardResponse = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(forwardRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        BigDecimal convertedValue = new BigDecimal(objectMapper.readTree(forwardResponse).get("result").asText());

        String reverseRequest = String.format("""
                {
                  "value": %s,
                  "sourceUnit": "feet",
                  "targetUnit": "metres"
                }
                """, convertedValue.toPlainString());

        // Act
        String reverseResponse = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reverseRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sourceUnit").value("feet"))
                .andExpect(jsonPath("$.targetUnit").value("metres"))
                .andExpect(jsonPath("$.result").isNumber())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        // Assert
        BigDecimal reversedValue = new BigDecimal(objectMapper.readTree(reverseResponse).get("result").asText());
        assertThat(reversedValue).isEqualByComparingTo(new BigDecimal("10.000000"));
    }

    @Test
    void should_return400_and_validationError_when_units_are_incompatible() throws Exception {
        // Arrange
        String requestBody = """
                {
                  "value": 10,
                  "sourceUnit": "metres",
                  "targetUnit": "gallons"
                }
                """;

        // Act / Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.message").value("Incompatible units: metres and gallons"))
                .andExpect(jsonPath("$.field").doesNotExist());
    }

    @Test
    void should_return400_and_validationError_when_value_is_non_numeric() throws Exception {
        // Arrange
        String requestBody = """
                {
                  "value": "abc",
                  "sourceUnit": "metres",
                  "targetUnit": "feet"
                }
                """;

        // Act / Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.message").value("Invalid value: expected a numeric JSON number"))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400_and_validationError_when_value_is_missing() throws Exception {
        // Arrange
        String requestBody = """
                {
                  "sourceUnit": "metres",
                  "targetUnit": "feet"
                }
                """;

        // Act / Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.message").value("Missing required field: value"))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400_and_validationError_when_sourceUnit_is_missing() throws Exception {
        // Arrange
        String requestBody = """
                {
                  "value": 10,
                  "targetUnit": "feet"
                }
                """;

        // Act / Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.message").value("Missing required field: sourceUnit"))
                .andExpect(jsonPath("$.field").value("sourceUnit"));
    }

    @Test
    void should_return400_and_validationError_when_targetUnit_is_missing() throws Exception {
        // Arrange
        String requestBody = """
                {
                  "value": 10,
                  "sourceUnit": "metres"
                }
                """;

        // Act / Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.message").value("Missing required field: targetUnit"))
                .andExpect(jsonPath("$.field").value("targetUnit"));
    }

    @Test
    void should_return400_and_validationError_when_unknownUnit_is_requested() throws Exception {
        // Arrange
        String requestBody = """
                {
                  "value": 10,
                  "sourceUnit": "yards",
                  "targetUnit": "feet"
                }
                """;

        // Act / Assert
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.message").value("Unknown unit: yards"))
                .andExpect(jsonPath("$.field").value("sourceUnit"));
    }

    @Test
    void should_return200_and_listAllSupportedUnits_when_getUnitsInvoked() throws Exception {
        // Arrange

        // Act / Assert
        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("metres"))
                .andExpect(jsonPath("$[0].system").value("metric"))
                .andExpect(jsonPath("$[1].name").value("feet"))
                .andExpect(jsonPath("$[1].system").value("imperial"))
                .andExpect(jsonPath("$[2].name").value("kilometres"))
                .andExpect(jsonPath("$[3].name").value("miles"))
                .andExpect(jsonPath("$[4].name").value("litres"))
                .andExpect(jsonPath("$[5].name").value("gallons"));
    }

    @Test
    void should_return404_when_batchEndpointIsRequested() throws Exception {
        // Arrange

        // Act / Assert
        mockMvc.perform(post("/api/convert/batch"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_allowAnonymousAccess_when_invokingEndpoints() throws Exception {
        // Arrange

        // Act / Assert
        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk());
    }
}
