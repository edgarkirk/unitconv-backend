package com.edgarkirk.unitconv.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;
import com.edgarkirk.unitconv.api.dto.response.ConversionResultResponse;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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

    @AfterEach
    void cleanUp() {
        conversionResultRepository.deleteAll();
    }

    @Test
    void should_return200_andPersistResult_when_validMetresToFeetConversion() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":10,\"sourceUnit\":\"metres\",\"targetUnit\":\"feet\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.inputValue").value(10))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andExpect(jsonPath("$.result").value(greaterThan(0.0)))
                .andReturn();

        ConversionResultResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ConversionResultResponse.class);
        assertThat(response.id()).isNotNull();
        assertThat(conversionResultRepository.count()).isEqualTo(1L);
    }

    @Test
    void should_return200_andApproximatelyOriginalValue_when_reverseFeetToMetresConversion() throws Exception {
        MvcResult forward = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":10,\"sourceUnit\":\"metres\",\"targetUnit\":\"feet\"}"))
                .andExpect(status().isOk())
                .andReturn();

        ConversionResultResponse forwardResponse = objectMapper.readValue(forward.getResponse().getContentAsString(), ConversionResultResponse.class);

        MvcResult reverse = mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":" + forwardResponse.result().toPlainString() + ",\"sourceUnit\":\"feet\",\"targetUnit\":\"metres\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sourceUnit").value("feet"))
                .andExpect(jsonPath("$.targetUnit").value("metres"))
                .andExpect(jsonPath("$.inputValue").value(forwardResponse.result().doubleValue()))
                .andReturn();

        ConversionResultResponse reverseResponse = objectMapper.readValue(reverse.getResponse().getContentAsString(), ConversionResultResponse.class);
        assertThat(reverseResponse.result().subtract(new BigDecimal("10")).abs()).isLessThanOrEqualTo(new BigDecimal("0.000001"));
    }

    @Test
    void should_return400_andValidationError_when_crossGroupUnitsProvided() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":10,\"sourceUnit\":\"metres\",\"targetUnit\":\"gallons\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Incompatible units: metres and gallons"))
                .andExpect(jsonPath("$.field").value("sourceUnit"));
    }

    @Test
    void should_return400_andValidationError_when_nonNumericValueProvided() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":\"abc\",\"sourceUnit\":\"metres\",\"targetUnit\":\"feet\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid numeric value for field value"))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400_andValidationError_when_valueMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sourceUnit\":\"metres\",\"targetUnit\":\"feet\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required field: value"))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400_andValidationError_when_sourceUnitMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":10,\"targetUnit\":\"feet\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required field: sourceUnit"))
                .andExpect(jsonPath("$.field").value("sourceUnit"));
    }

    @Test
    void should_return400_andValidationError_when_targetUnitMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":10,\"sourceUnit\":\"metres\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required field: targetUnit"))
                .andExpect(jsonPath("$.field").value("targetUnit"));
    }

    @Test
    void should_persistExactlyOneConversionResult_when_validConversionSubmitted() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":10,\"sourceUnit\":\"metres\",\"targetUnit\":\"feet\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());

        assertThat(conversionResultRepository.count()).isEqualTo(1L);
    }

    @Test
    void should_returnAllSixUnits_when_getUnits() throws Exception {
        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("feet"))
                .andExpect(jsonPath("$[1].name").value("gallons"))
                .andExpect(jsonPath("$[2].name").value("kilometres"))
                .andExpect(jsonPath("$[3].name").value("litres"))
                .andExpect(jsonPath("$[4].name").value("metres"))
                .andExpect(jsonPath("$[5].name").value("miles"));
    }

    @Test
    void should_returnUnitObjectsWithExactFields_when_getUnits() throws Exception {
        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].name").isNotEmpty())
                .andExpect(jsonPath("$[0].system").value("imperial"));
    }

    @Test
    void should_return404_when_authenticationAndUserEndpointsRequested() throws Exception {
        mockMvc.perform(post("/api/auth/login")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/users")).andExpect(status().isNotFound());
    }

    @Test
    void should_return404_when_batchConversionEndpointRequested() throws Exception {
        mockMvc.perform(post("/api/convert/batch")).andExpect(status().isNotFound());
    }

    @Test
    void should_return405_when_unitCreationEndpointRequested() throws Exception {
        mockMvc.perform(post("/api/units")).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void should_return404_when_unitUpdateAndDeletionEndpointsRequested() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(put("/api/units/{id}", id)).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/units/{id}", id)).andExpect(status().isNotFound());
    }
}
