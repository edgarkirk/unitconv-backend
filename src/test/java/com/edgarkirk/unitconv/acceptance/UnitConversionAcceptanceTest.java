package com.edgarkirk.unitconv.acceptance;

import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UnitConversionAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConversionResultRepository conversionResultRepository;

    @Test
    void should_returnConversionResult_when_payloadIsValid() throws Exception {
        long before = conversionResultRepository.count();

        mockMvc.perform(post("/api/convert")
                        .contentType("application/json")
                        .content("""
                                {"value":1.0,"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.inputValue").value(1.0))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andExpect(jsonPath("$.result").isNumber());

        assertThat(conversionResultRepository.count()).isEqualTo(before + 1);
    }

    @Test
    void should_roundTripMetresAndFeetWithinTolerance() throws Exception {
        var metresToFeet = mockMvc.perform(post("/api/convert")
                        .contentType("application/json")
                        .content("""
                                {"value":1.0,"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number feetValue = JsonPath.read(metresToFeet, "$.result");
        mockMvc.perform(post("/api/convert")
                        .contentType("application/json")
                        .content("""
                                {"value":%s,"sourceUnit":"feet","targetUnit":"metres"}
                                """.formatted(feetValue)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(org.hamcrest.Matchers.closeTo(1.0, 0.0001)));
    }

    @Test
    void should_returnValidationError_when_unitsAreIncompatible() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType("application/json")
                        .content("""
                                {"value":1.0,"sourceUnit":"metres","targetUnit":"gallons"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("incompatible")));
    }

    @Test
    void should_returnValidationError_when_valueIsNotNumeric() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType("application/json")
                        .content("""
                                {"value":"abc","sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("numeric")));
    }

    @Test
    void should_returnValidationError_when_requiredFieldsAreMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void should_returnSupportedUnits_when_requestingUnits() throws Exception {
        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[*].name").isNotEmpty());
    }
}
