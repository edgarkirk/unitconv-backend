package com.edgarkirk.unitconv.acceptance;

import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.api.dto.response.UnitResponse;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ConversionApiAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConversionResultRepository conversionResultRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Test
    void should_return200AndPersistConversionResult_when_validMetresToFeetConversion() throws Exception {
        long before = conversionResultRepository.count();

        String responseBody = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":12.5,"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.inputValue").value(12.5))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ConversionResultResponse response = objectMapper.readValue(responseBody, ConversionResultResponse.class);
        assertThat(response.id()).isNotNull();
        assertThat(conversionResultRepository.count()).isEqualTo(before + 1);
    }

    @Test
    void should_returnApproximateOriginalValue_when_reverseMetresToFeetConversion() throws Exception {
        String forwardBody = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":12.5,"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ConversionResultResponse forward = objectMapper.readValue(forwardBody, ConversionResultResponse.class);
        String reversePayload = "{\"value\":" + forward.result() + ",\"sourceUnit\":\"feet\",\"targetUnit\":\"metres\"}";
        String reverseBody = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reversePayload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ConversionResultResponse reverse = objectMapper.readValue(reverseBody, ConversionResultResponse.class);
        assertThat(reverse.result().subtract(BigDecimal.valueOf(12.5)).abs())
                .isLessThanOrEqualTo(new BigDecimal("0.000001"));
    }

    @Test
    void should_return400AndValidationError_when_convertingIncompatibleUnits() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":1,"sourceUnit":"metres","targetUnit":"gallons"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Incompatible units: metres to gallons"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void should_return400AndValidationError_when_valueIsNonNumeric() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":"abc","sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid numeric value"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void should_return400AndValidationError_when_valueFieldIsMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required field: value"))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400AndValidationError_when_sourceUnitFieldIsMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":12.5,"targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required field: sourceUnit"))
                .andExpect(jsonPath("$.field").value("sourceUnit"));
    }

    @Test
    void should_return400AndValidationError_when_targetUnitFieldIsMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":12.5,"sourceUnit":"metres"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required field: targetUnit"))
                .andExpect(jsonPath("$.field").value("targetUnit"));
    }

    @Test
    void should_return200AndAllSupportedUnits_when_gettingUnits() throws Exception {
        String responseBody = mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UnitResponse[] units = objectMapper.readValue(responseBody, UnitResponse[].class);
        assertThat(List.of(units))
                .hasSize(6)
                .extracting(UnitResponse::name)
                .containsExactly("metres", "feet", "kilometres", "miles", "litres", "gallons");
        assertThat(List.of(units))
                .extracting(UnitResponse::system)
                .containsExactly("metric", "imperial", "metric", "imperial", "metric", "imperial");
    }

    @Test
    void should_return404_when_requestingAuthAndUserEndpoints() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/users")).andExpect(status().isNotFound());
    }

    @Test
    void should_return400AndValidationError_when_unsupportedUnitNameProvided() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":1,"sourceUnit":"parsecs","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported unit: parsecs"));
    }

    @Test
    void should_return400AndValidationError_when_batchPayloadSubmitted() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [{"value":1,"sourceUnit":"metres","targetUnit":"feet"}]
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Batch conversions are not supported"));
    }
}
