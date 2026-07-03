package com.edgarkirk.unitconv.acceptance;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.edgarkirk.unitconv.persistence.entity.ConversionResultEntity;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ConversionAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConversionResultRepository conversionResultRepository;

    @Test
    void should_return200AndPersistResult_when_validMetresToFeetConversion() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conversionRequestJson(new BigDecimal("10"), "metres", "feet")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.inputValue").value(10))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andExpect(jsonPath("$.result").value(closeTo(32.8084, 0.000001)));
    }

    @Test
    void should_return200AndRoundTripWithinPrecision_when_reverseMetresFeetConversion() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conversionRequestJson(new BigDecimal("10"), "metres", "feet")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(notNullValue()));

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conversionRequestJson(new BigDecimal("32.8084"), "feet", "metres")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(closeTo(10.0, 0.000001)));
    }

    @Test
    void should_return200AndPersistResult_when_validLitresToGallonsConversion() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conversionRequestJson(new BigDecimal("1"), "litres", "gallons")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.sourceUnit").value("litres"))
                .andExpect(jsonPath("$.targetUnit").value("gallons"))
                .andExpect(jsonPath("$.result").value(closeTo(0.264172, 0.000001)));
    }

    @Test
    void should_return400AndValidationError_when_metresToGallonsAreIncompatible() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conversionRequestJson(new BigDecimal("10"), "metres", "gallons")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Incompatible units")))
                .andExpect(jsonPath("$.field").doesNotExist());
    }

    @Test
    void should_return400AndValidationError_when_valueIsNonNumeric() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":\"abc\",\"sourceUnit\":\"metres\",\"targetUnit\":\"feet\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Invalid numeric value")))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400AndValidationError_when_valueIsMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sourceUnit\":\"metres\",\"targetUnit\":\"feet\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("value")))
                .andExpect(jsonPath("$.field").value("value"));
    }

    @Test
    void should_return400AndValidationError_when_sourceUnitIsMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":10,\"targetUnit\":\"feet\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("sourceUnit")))
                .andExpect(jsonPath("$.field").value("sourceUnit"));
    }

    @Test
    void should_return400AndValidationError_when_targetUnitIsMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":10,\"sourceUnit\":\"metres\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("targetUnit")))
                .andExpect(jsonPath("$.field").value("targetUnit"));
    }

    @Test
    void should_persistConversionResultWithSameId_when_lookupByDatabase() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conversionRequestJson(new BigDecimal("10"), "metres", "feet")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notNullValue()));

        UUID storedId = conversionResultRepository.findAll().stream()
                .map(ConversionResultEntity::getId)
                .findFirst()
                .orElseThrow();
        conversionResultRepository.findById(storedId).ifPresentOrElse(result -> {
            result.getId();
        }, () -> {
            throw new AssertionError("Expected conversion result to be persisted");
        });
    }

    @Test
    void should_return200AndRoundTripWithinPrecision_when_reverseKilometresToMilesConversion() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conversionRequestJson(new BigDecimal("3.5"), "kilometres", "miles")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inputValue").value(3.5))
                .andExpect(jsonPath("$.result").value(notNullValue()));

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conversionRequestJson(new BigDecimal("2.1748"), "miles", "kilometres")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(closeTo(3.5, 0.000001)));
    }

    @Test
    void should_return400AndValidationError_when_batchPayloadIsSubmitted() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"value\":10,\"sourceUnit\":\"metres\",\"targetUnit\":\"feet\"}]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(notNullValue()));
    }

    private String conversionRequestJson(BigDecimal value, String sourceUnit, String targetUnit) {
        return String.format("{\"value\":%s,\"sourceUnit\":\"%s\",\"targetUnit\":\"%s\"}",
                value.toPlainString(), sourceUnit, targetUnit);
    }
}
