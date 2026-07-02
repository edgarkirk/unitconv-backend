package com.edgarkirk.unitconv.acceptance;

import com.edgarkirk.unitconv.UnitConvApplication;
import com.edgarkirk.unitconv.persistence.repository.ConversionResultRepository;
import com.edgarkirk.unitconv.persistence.repository.UnitRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = UnitConvApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ConversionAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConversionResultRepository conversionResultRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Test
    void should_return200AndPersistConversion_when_validMetresToFeetRequest() throws Exception {
        long initialCount = conversionResultRepository.count();

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":10,"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(matchesPattern("^[0-9a-fA-F-]{36}$")))
                .andExpect(jsonPath("$.inputValue").value(10))
                .andExpect(jsonPath("$.sourceUnit").value("metres"))
                .andExpect(jsonPath("$.targetUnit").value("feet"))
                .andExpect(jsonPath("$.result").isNumber());

        assertThat(conversionResultRepository.count()).isEqualTo(initialCount + 1);
    }

    @Test
    void should_returnApproximateMetres_when_convertingFeetToMetres() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":3.28084,"sourceUnit":"feet","targetUnit":"metres"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(closeTo(1.0, 0.000001)));
    }

    @Test
    void should_return400AndValidationError_when_unitsAreIncompatible() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":10,"sourceUnit":"metres","targetUnit":"gallons"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").value(matchesPattern("^[0-9a-fA-F-]{36}$")))
                .andExpect(jsonPath("$.message").value("Incompatible units: metres cannot be converted to gallons"))
                .andExpect(jsonPath("$.field").doesNotExist());
    }

    @Test
    void should_return400AndValidationError_when_valueIsNonNumeric() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":"abc","sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid numeric value: value must be a number"));
    }

    @Test
    void should_return400AndValidationError_when_requiredFieldIsMissing() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sourceUnit":"metres","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("value"))
                .andExpect(jsonPath("$.message").value("Missing required field: value"));
    }

    @Test
    void should_returnAllSixUnits_when_listingSupportedUnits() throws Exception {
        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].name").exists())
                .andExpect(jsonPath("$[*].system").exists());

        assertThat(unitRepository.findAll()).hasSize(6);
        assertThat(unitRepository.findAll().stream().map(com.edgarkirk.unitconv.persistence.entity.Unit::getName).collect(Collectors.toSet()))
                .isEqualTo(Set.of("metres", "feet", "kilometres", "miles", "litres", "gallons"));
    }

    @Test
    void should_persistConversionResultBeforeResponding_when_conversionSucceeds() throws Exception {
        long initialCount = conversionResultRepository.count();

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":5,"sourceUnit":"litres","targetUnit":"gallons"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        assertThat(conversionResultRepository.count()).isEqualTo(initialCount + 1);
    }

    @Test
    void should_return400AndNotPersist_when_sourceUnitIsUnsupported() throws Exception {
        long initialCount = conversionResultRepository.count();

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":10,"sourceUnit":"yards","targetUnit":"feet"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported unit: yards"));

        assertThat(conversionResultRepository.count()).isEqualTo(initialCount);
    }

    @Test
    void should_return400AndNotPersist_when_targetUnitIsUnsupported() throws Exception {
        long initialCount = conversionResultRepository.count();

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":10,"sourceUnit":"metres","targetUnit":"chains"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported unit: chains"));

        assertThat(conversionResultRepository.count()).isEqualTo(initialCount);
    }

    @Test
    void should_return400AndValidationErrorWithUuid_when_validationFails() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":10,"sourceUnit":"metres","targetUnit":"gallons"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").value(matchesPattern("^[0-9a-fA-F-]{36}$")))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
