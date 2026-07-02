package com.edgarkirk.unitconv.acceptance;

import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Spliterators;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ConversionApiAcceptanceTest {

    private static final String CONVERT_PATH = "/api/convert";
    private static final String BATCH_PATH = "/api/batch-convert";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConversionResultRepository conversionResultRepository;

    @Test
    void should_return200_andPersistConversionResult_when_validConversionRequest() throws Exception {
        long beforeCount = conversionResultRepository.count();

        String response = mockMvc.perform(post(CONVERT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":10,"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", matchesPattern("[0-9a-fA-F-]{36}")))
                .andExpect(jsonPath("$.inputValue").value(10))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andExpect(jsonPath("$.result").isNumber())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode responseJson = objectMapper.readTree(response);
        assertThat(fieldNames(responseJson)).containsExactlyInAnyOrder("id", "inputValue", "sourceUnit", "targetUnit", "result");
        assertThat(UUID.fromString(responseJson.get("id").asText())).isNotNull();
        assertThat(conversionResultRepository.count()).isEqualTo(beforeCount + 1);
    }

    private static java.util.Set<String> fieldNames(JsonNode node) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(node.fieldNames(), 0), false)
                .collect(Collectors.toSet());
    }

    @Test
    void should_return200_andPreserveReverseConversionPrecision_when_roundTrippingMetresAndFeet() throws Exception {
        String firstResponse = mockMvc.perform(post(CONVERT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":10,"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode firstJson = objectMapper.readTree(firstResponse);
        BigDecimal feetValue = firstJson.get("result").decimalValue();

        String secondResponse = mockMvc.perform(post(CONVERT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"value\":%s,\"sourceUnit\":\"feet\",\"targetUnit\":\"metres\"}", feetValue.toPlainString())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode secondJson = objectMapper.readTree(secondResponse);
        BigDecimal metresValue = secondJson.get("result").decimalValue();

        assertThat(metresValue.subtract(BigDecimal.TEN).abs()).isLessThanOrEqualTo(new BigDecimal("0.000001"));
    }

    @Test
    void should_return400_andValidationError_when_unitsAreIncompatible() throws Exception {
        mockMvc.perform(post(CONVERT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":10,"sourceUnit":"metres","targetUnit":"gallons"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id", matchesPattern("[0-9a-fA-F-]{36}")))
                .andExpect(jsonPath("$.message").value("Incompatible units: metres and gallons"))
                .andExpect(jsonPath("$.field").value((String) null));
    }

    @Test
    void should_return400_andValidationError_when_valueIsNonNumeric() throws Exception {
        mockMvc.perform(post(CONVERT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":"abc","sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id", matchesPattern("[0-9a-fA-F-]{36}")))
                .andExpect(jsonPath("$.message").value("value must be numeric"))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400_andValidationError_when_valueIsMissing() throws Exception {
        mockMvc.perform(post(CONVERT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id", matchesPattern("[0-9a-fA-F-]{36}")))
                .andExpect(jsonPath("$.message").value("value is required"))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400_andValidationError_when_sourceUnitIsMissing() throws Exception {
        mockMvc.perform(post(CONVERT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":10,"targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id", matchesPattern("[0-9a-fA-F-]{36}")))
                .andExpect(jsonPath("$.message").value("sourceUnit is required"))
                .andExpect(jsonPath("$.field").value("sourceUnit"));
    }

    @Test
    void should_return400_andValidationError_when_targetUnitIsMissing() throws Exception {
        mockMvc.perform(post(CONVERT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":10,"sourceUnit":"metres"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id", matchesPattern("[0-9a-fA-F-]{36}")))
                .andExpect(jsonPath("$.message").value("targetUnit is required"))
                .andExpect(jsonPath("$.field").value("targetUnit"));
    }

    @Test
    void should_return404_when_apiSurfaceContainsNoBatchEndpoint() throws Exception {
        mockMvc.perform(get(BATCH_PATH))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_notPersistConversionResult_when_requestIsInvalid() throws Exception {
        long beforeCount = conversionResultRepository.count();

        mockMvc.perform(post(CONVERT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":10,"sourceUnit":"metres","targetUnit":"gallons"}
                                """))
                .andExpect(status().isBadRequest());

        assertThat(conversionResultRepository.count()).isEqualTo(beforeCount);
    }

    @Test
    void should_generateServerUuid_when_conversionSucceeds() throws Exception {
        String response = mockMvc.perform(post(CONVERT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":10,"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        assertThat(UUID.fromString(json.get("id").asText())).isNotNull();
    }
}
